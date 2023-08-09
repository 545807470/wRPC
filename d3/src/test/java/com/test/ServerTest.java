package com.test;

import com.wyq.rpc.provider.ProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerTest {
    public static void main(String[] args) {
        //创建两个线程组bossGroup和workerGroup, 含有的子线程NioEventLoop的个数默认为cpu核数的两倍
        // bossGroup只是处理连接请求 ,真正的和客户端业务处理，会交给workerGroup完成
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//默认初始化一个线程
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
//                            ch.pipeline().addLast(new ProviderHandler());
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
