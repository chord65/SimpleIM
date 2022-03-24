package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.chat.notify.P2pChatNotifyPacket;
import org.chord.sim.common.protocol.chat.response.MessageReceivedResponsePacket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chord
 * date 2022/2/2 21:34
 * function:
 */
@ChannelHandler.Sharable
public class P2pChatNotifyHandler extends SimpleChannelInboundHandler<P2pChatNotifyPacket> {

    public static final P2pChatNotifyHandler INSTANCE = new P2pChatNotifyHandler();

    private SIMClient simClient;

    private P2pChatNotifyHandler() {
        simClient = SpringUtils.getBean(SIMClient.class);
    };

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, P2pChatNotifyPacket notifyPacket) throws Exception {

        Message message = notifyPacket.getMessage();

        // 消息接收响应包
        MessageReceivedResponsePacket receivedResponsePacket = new MessageReceivedResponsePacket();
        receivedResponsePacket.setToUserId(simClient.getUserInfo().getUserId());
        List<String> msgIdList = new ArrayList<>();

        if (message != null) {
            new Thread(()->{
                System.out.format("%s(%s) %s\n", message.getFromUserName(), message.getFromId(), new Date(Long.parseLong(message.getSeqNumber())));
                System.out.println(message.getMsgContent());
            }).start();
            msgIdList.add(message.getMsgId());
        }

        receivedResponsePacket.setMsgIdList(msgIdList);

        // 发送响应包
        ctx.writeAndFlush(receivedResponsePacket);
    }

}
