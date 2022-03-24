package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.chat.response.MessageReceivedResponsePacket;
import org.chord.sim.common.protocol.chat.response.PullMessagesResponsePacket;

import java.util.ArrayList;
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

    private SIMClient simClient;

    private PullMessagesResponseHandler() {
        simClient = SpringUtils.getBean(SIMClient.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PullMessagesResponsePacket responsePacket) throws Exception {

        List<Message> messages = responsePacket.getMessages();

        if (messages != null) {
            // 消息接收响应包
            MessageReceivedResponsePacket receivedResponsePacket = new MessageReceivedResponsePacket();
            receivedResponsePacket.setToUserId(simClient.getUserInfo().getUserId());
            List<String> msgIdList = new ArrayList<>();

            new Thread(() -> {
                for (Message message : messages) {
                    System.out.format("%s(%s) %s\n", message.getFromUserName(), message.getFromId(), new Date(Long.parseLong(message.getSeqNumber())));
                    System.out.println(message.getMsgContent());

                    msgIdList.add(message.getMsgId());
                }
            }).start();

            receivedResponsePacket.setMsgIdList(msgIdList);

            // 发送响应包
            ctx.writeAndFlush(receivedResponsePacket);
        }
    }
}
