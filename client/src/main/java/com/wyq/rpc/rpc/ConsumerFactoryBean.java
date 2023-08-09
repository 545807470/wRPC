package com.wyq.rpc.rpc;

import com.wyq.common.pojo.MyRpcRequestMessage;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ConsumerFactoryBean implements FactoryBean<Object> {

    private Integer port;

    private String host;

    private Class interfaceClass;

    @Override
    public Object getObject() throws Exception {
        Object proxyInstance = Proxy.newProxyInstance(ConsumerFactoryBean.class.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                int requestId = ConsumerHandler.ATOMIC_INTEGER.incrementAndGet();
                //1.构建请求对象
                MyRpcRequestMessage request = new MyRpcRequestMessage();
                request.setMethodName(method.getName());
                request.setParameters(args);
                request.setParameterTypes(method.getParameterTypes());
                request.setRequestId(requestId);
                //2.准备接收结果Promise
                DefaultPromise<Object> promise = new DefaultPromise<>(new DefaultEventLoop());
                ConsumerHandler.PROMISE_MAP.put(requestId,promise);
                //3.发送网络请求
                NettyClient nettyClient = new NettyClient(port, host);
                nettyClient.sendRequest(request);
                //4.等待promise结果
                promise.await();
                if (promise.isSuccess()){
                    //执行成功
                    return promise.getNow();
                }else {
                    //执行失败
                    Throwable cause = promise.cause();
                    throw new RuntimeException(cause);
                }
            }
        });

        return proxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }
}
