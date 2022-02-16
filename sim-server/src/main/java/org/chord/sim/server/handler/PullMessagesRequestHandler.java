package org.chord.sim.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.request.PullMessagesRequestPacket;
import org.chord.sim.common.protocol.response.PullMessagesResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.server.entity.Message;
import org.chord.sim.server.service.MessageService;
import org.chord.sim.server.service.UserService;
import org.chord.sim.server.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chord
 * date 2022/2/16 22:32
 * function:
 */

@ChannelHandler.Sharable
public class PullMessagesRequestHandler extends SimpleChannelInboundHandler<PullMessagesRequestPacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullMessagesRequestHandler.class);

    public static final PullMessagesRequestHandler INSTANCE = new PullMessagesRequestHandler();

    private MessageService messageService;

    private UserService userService;

    private PullMessagesRequestHandler() {
        messageService = SpringUtils.getBean(MessageService.class);
        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PullMessagesRequestPacket request) throws Exception {

        LOGGER.info("pull messages request from {}", request.getToUserId());

        PullMessagesResponsePacket response = new PullMessagesResponsePacket();
        response.setStatus(Status.OK);
        response.setRequestSeqNumber(request.getSeqNumber());

        // 获取发起请求的用户的离线消息
        List<Message> messages = messageService.findOfflineMessageByToId(request.getToUserId());
        List<org.chord.sim.common.pojo.Message> pojoMessages = new ArrayList<>();
        for (Message message : messages) {
            org.chord.sim.common.pojo.Message pojoMessage = new org.chord.sim.common.pojo.Message();
            String fromUserId = message.getFromId();
            pojoMessage.setFromId(fromUserId);
            pojoMessage.setFromUserName(userService.getUserNameById(fromUserId));
            pojoMessage.setMsgId(message.getMsgId());
            pojoMessage.setSeqNumber(message.getSeqNumber());
            pojoMessage.setMsgType(message.getMsgType());
            pojoMessage.setMsgContent(message.getMsgContent());

            pojoMessages.add(pojoMessage);
        }
        response.setMessages(pojoMessages);

        // 向channel写入响应包
        ctx.writeAndFlush(response);
    }
}
