package com.wyq.rpc.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProviderBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
        //如果是服务提供类则创建代理对象
        if (bean.getClass().isAnnotationPresent(Provider.class)){
//            Provider provider = bean.getClass().getAnnotation(Provider.class);
//            int port = provider.port();

//            providerStart(port,bean);
            //替换成代理对象
            providerStart(bean);
        }

        return bean;
    }

    private Integer num = 0;

//    private <T> T createProxy(Object serverBean){
//        return (T) Proxy.newProxyInstance(serverBean.getClass().getClassLoader(), serverBean.getClass().getInterfaces(), new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                //只启动一次服务端
//                providerStart(serverBean);
//                return method.invoke(serverBean,args);
//            }
//        });
//    }

    public void providerStart(/*int port,*/Object bean){
        //创建两个线程组bossGroup和workerGroup, 含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,真正的和客户端业务处理，会交给workerGroup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup();//默认初始化一个线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();//不带参数，默认初始化CPU核数两倍的线程数
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建通道初始化对象，设置初始化参数
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //字符串解码器，接收的方法名以及方法可能需要的参数
//                            ch.pipeline().addLast(new MyMessageDecoder());//入站，自定义协议解码
                            ch.pipeline().addLast(new ObjectEncoder());//出站，对象编码
                            //对象解码器
                            ch.pipeline().addLast(new ObjectDecoder(102400, ClassResolvers.cacheDisabled(ProviderBeanPostProcessor.class.getClassLoader())));
                            ch.pipeline().addLast(new ProviderHandler(bean));
                        }
                    });
            System.out.println("服务端启动...");
            ChannelFuture cf = bootstrap.bind(9000).sync();//启动服务器
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //最终，都会关闭两个线程组释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
