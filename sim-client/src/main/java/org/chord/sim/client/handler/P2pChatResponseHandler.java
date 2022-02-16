package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.response.P2pChatResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;

/**
 * @author chord
 * date 2022/2/2 21:23
 * function:
 */
@ChannelHandler.Sharable
public class P2pChatResponseHandler extends SimpleChannelInboundHandler<P2pChatResponsePacket> {

    public static final P2pChatResponseHandler INSTANCE = new P2pChatResponseHandler();

    private P2pChatResponseHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, P2pChatResponsePacket responsePacket) throws Exception {
        if (responsePacket.getStatus() == Status.OK) {

            // 后期可以增加重传的逻辑

            new Thread(()->{
                System.out.println(responsePacket.getMsg());
            }).start();
        }
        else {
            new Thread(()->{
                System.out.println(responsePacket.getMsg());
            }).start();
        }
    }
}
