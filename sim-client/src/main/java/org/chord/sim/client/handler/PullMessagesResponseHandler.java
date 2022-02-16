package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.response.PullMessagesResponsePacket;

import java.util.Date;
import java.util.List;

/**
 * @author chord
 * date 2022/2/16 23:24
 * function:
 */
@ChannelHandler.Sharable
public class PullMessagesResponseHandler extends SimpleChannelInboundHandler<PullMessagesResponsePacket> {

    public static final PullMessagesResponseHandler INSTANCE = new PullMessagesResponseHandler();

    private PullMessagesResponseHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PullMessagesResponsePacket responsePacket) throws Exception {

        List<Message> messages = responsePacket.getMessages();

        new Thread(()->{
            for (Message message : messages) {
                System.out.format("%s(%s) %s\n", message.getFromUserName(), message.getFromId(), new Date(Long.parseLong(message.getSeqNumber())));
                System.out.println(message.getMsgContent());
            }
        }).start();

    }
}
