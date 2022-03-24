package org.chord.sim.rpc.connect.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.rpc.common.asyn.RpcFuture;
import org.chord.sim.rpc.protocol.RequestPacket;
import org.chord.sim.rpc.protocol.ResponsePacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * @author chord
 * date 2022/3/19 23:12
 * function:
 */
public class NettyAsynHandler extends SimpleChannelInboundHandler<ResponsePacket> {

    private Channel channel;

    // 存放requestId对应的Future对象
    private ConcurrentHashMap<String, RpcFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponsePacket responsePacket) throws Exception {
        if (responsePacket == null) return;

        String requestId = responsePacket.getRequestId();
        RpcFuture future = futureMap.getOrDefault(requestId, null);
        if (future != null) {
            // 通过done()通知等待响应的线程
            future.done(responsePacket);
            // 一次交互已经完成了，移除该future
            futureMap.remove(requestId);
        }
    }

    /**
     * 发送一个request，并立即返回一个future，用于异步获取对应的response
     */
    public RpcFuture sendRequest(RequestPacket requestPacket) throws InterruptedException {
        RpcFuture future = new RpcFuture(requestPacket);
        futureMap.put(requestPacket.getRequestId(), future);
        ChannelFuture future1 = this.channel.writeAndFlush(requestPacket);
        while(!future1.isSuccess()) {}
        System.out.println("RPC : send request " + requestPacket.getRequestId());
        return future;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        System.out.println("RPC : connect to " + this.channel.remoteAddress().toString());
    }


}
