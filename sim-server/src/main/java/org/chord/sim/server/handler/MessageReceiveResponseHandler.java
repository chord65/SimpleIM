package org.chord.sim.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.chat.response.MessageReceivedResponsePacket;
import org.chord.sim.server.service.MessageService;
import org.chord.sim.server.util.SpringUtils;

import java.util.List;

/**
 * @author chord
 * date 2022/2/18 0:06
 * function:
 */
@ChannelHandler.Sharable
public class MessageReceiveResponseHandler extends SimpleChannelInboundHandler<MessageReceivedResponsePacket> {

    public static final MessageReceiveResponseHandler INSTANCE = new MessageReceiveResponseHandler();

    private MessageService messageService;

    private MessageReceiveResponseHandler() {

        messageService = SpringUtils.getBean(MessageService.class);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageReceivedResponsePacket responsePacket) throws Exception {

        List<String> msgIdList = responsePacket.getMsgIdList();

        if (msgIdList == null) return;

        for (String msgId : msgIdList) {
            messageService.updateStatus(msgId, 1);
        }

    }
}
