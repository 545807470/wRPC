package com.wyq.rpc.rpc;

import com.wyq.common.pojo.MyRpcRequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient {

    private int port;

    private String host;

    private Bootstrap bootstrap;

    private EventLoopGroup group = new NioEventLoopGroup();

    public NettyClient(int port, String host) {
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ObjectEncoder());//出站，对象编码
                        //对象解码器
                        ch.pipeline().addLast(new ObjectDecoder(102400, ClassResolvers.cacheDisabled(NettyClient.class.getClassLoader())));
                        ch.pipeline().addLast(new ConsumerHandler());
                    }
                });

        this.port = port;
        this.host = host;
    }

    public void sendRequest(MyRpcRequestMessage rpcRequestMessage){
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()){
                    //连接成功
                    System.out.println("客户端启动");
                }else {
                    //连接失败
                    future.cause().printStackTrace();
                    group.shutdownGracefully();
                }
            });
            //发送消息
            channelFuture.channel().writeAndFlush(rpcRequestMessage);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
