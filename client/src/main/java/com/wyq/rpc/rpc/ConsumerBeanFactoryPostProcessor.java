package com.wyq.rpc.rpc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConsumerBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private Map<String,BeanDefinition> consumerBeanDefinition = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //1.拿到所有的
        for (String beanDefinitionName : configurableListableBeanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanDefinitionName);
            //拿到对应的全限定名
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null){
                //拿到当前类的字节码对象
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                //检测所有的字段对象
                ReflectionUtils.doWithFields(clazz, this::checkClazz);
            }
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) configurableListableBeanFactory;
            //把解析到的客户端代理对象注册进spring容器中
            for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : consumerBeanDefinition.entrySet()) {
                if (applicationContext.containsBean(stringBeanDefinitionEntry.getKey())){
                    return;
                }
                registry.registerBeanDefinition(stringBeanDefinitionEntry.getKey(),stringBeanDefinitionEntry.getValue());
            }
        }
    }

    private void checkClazz(Field field){
        if (field.isAnnotationPresent(Consumer.class)){
            Consumer consumer = field.getAnnotation(Consumer.class);
            //拿到字段的类型也就是代理对象的类型
            Class<?> proxyType = field.getType();

            //构建代理对象
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerFactoryBean.class);
            builder.addPropertyValue("port",consumer.port());
            builder.addPropertyValue("host",consumer.host());
            builder.addPropertyValue("interfaceClass",proxyType);

            consumerBeanDefinition.put(field.getName(), builder.getBeanDefinition());
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
