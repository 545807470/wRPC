package com.wyq.rpc.provider;

import com.wyq.common.pojo.MyRpcRequestMessage;
import com.wyq.common.pojo.MyRpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class ProviderHandler extends SimpleChannelInboundHandler<MyRpcRequestMessage> {

    private Object bean;
    public ProviderHandler(Object bean){
        this.bean = bean;
    }

    /**
     * 异常关闭或断开连接时触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 接收到数据时触发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyRpcRequestMessage msg) {

        MyRpcResponseMessage response = new MyRpcResponseMessage();
        try {
            String methodName = msg.getMethodName();
            Method method = bean.getClass().getMethod(methodName, msg.getParameterTypes());
            Object result = method.invoke(bean, msg.getParameters());

            response.setResult(result);
            response.setResponseId(msg.getRequestId());
            //执行结果返回给客户端
            ctx.channel().writeAndFlush(response);
        } catch (Exception e) {
            //异常结果返回给客户端
            response.setExceptionResult(e);
            response.setResponseId(msg.getRequestId());
            ctx.channel().writeAndFlush(response);
        }
    }
}
