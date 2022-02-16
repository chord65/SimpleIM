package org.chord.sim.server.service;

import com.mysql.cj.protocol.MessageReader;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.notify.P2pChatNotifyPacket;
import org.chord.sim.common.protocol.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.response.P2pChatResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.common.util.SIMUtils;
import org.chord.sim.server.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author chord
 * date 2022/2/2 21:58
 * function:
 */
@Service
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    public P2pChatResponsePacket P2pChat(P2pChatRequestPacket requestPacket) {
        String fromUserId = requestPacket.getFromUserId();
        String toUserId = requestPacket.getToUserId();

        P2pChatResponsePacket responsePacket = new P2pChatResponsePacket();
        responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());

        // 创建消息对象
        String msgId = SIMUtils.generateUUID();
        Message message = new Message();
        message.setFromId(fromUserId);
        message.setToId(toUserId);
        message.setMsgId(msgId);
        message.setSeqNumber(String.valueOf(requestPacket.getSeqNumber()));
        message.setMsgType(0);
        message.setStatus(0);
        message.setMsgContent(requestPacket.getMsg());

        // 获取目标用户的channel
        NioSocketChannel channel = sessionService.getChannel(toUserId);
        if (channel == null) {
            if (userService.getUserById(toUserId) == null) {
                responsePacket.setStatus(Status.FAILED);
                responsePacket.setMsg("目标用户不存在，发送失败");
                return responsePacket;
            }
        }
        else {
            // 创建消息通知包
            P2pChatNotifyPacket notifyPacket = new P2pChatNotifyPacket();

            org.chord.sim.common.pojo.Message pojoMessage = new org.chord.sim.common.pojo.Message();
            pojoMessage.setMsgContent(requestPacket.getMsg());
            pojoMessage.setMsgId(msgId);
            pojoMessage.setFromId(fromUserId);
            pojoMessage.setFromUserName(sessionService.getSession(fromUserId).getUserName());
            pojoMessage.setSeqNumber(String.valueOf(requestPacket.getSeqNumber()));

            notifyPacket.setMessage(pojoMessage);

            // 发送消息
            channel.writeAndFlush(notifyPacket);

            LOGGER.info("p2p_chat: {} -> {}", fromUserId, toUserId);
        }

        // 存储消息到mysql数据库
        messageService.addMessage(message);

        responsePacket.setStatus(Status.OK);
        responsePacket.setMsg("消息发送成功！");

        return responsePacket;
    }

}
