package com.wyq.rpc.rpc;

import com.wyq.common.pojo.MyRpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerHandler extends SimpleChannelInboundHandler<MyRpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyRpcResponseMessage msg) throws Exception {

        Integer responseId = msg.getResponseId();

        Promise<Object> promise = PROMISE_MAP.remove(responseId);

        promise.setSuccess(msg.getResult());
    }
}
