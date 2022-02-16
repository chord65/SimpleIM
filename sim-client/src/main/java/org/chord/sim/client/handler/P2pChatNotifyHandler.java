package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.notify.P2pChatNotifyPacket;

import java.util.Date;

/**
 * @author chord
 * date 2022/2/2 21:34
 * function:
 */
@ChannelHandler.Sharable
public class P2pChatNotifyHandler extends SimpleChannelInboundHandler<P2pChatNotifyPacket> {

    public static final P2pChatNotifyHandler INSTANCE = new P2pChatNotifyHandler();

    private P2pChatNotifyHandler() {};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, P2pChatNotifyPacket notifyPacket) throws Exception {

        Message message = notifyPacket.getMessage();

        new Thread(()->{
            System.out.format("%s(%s) %s\n", message.getFromUserName(), message.getFromId(), new Date(Long.parseLong(message.getSeqNumber())));
            System.out.println(message.getMsgContent());
        }).start();
    }

}
