package org.chord.sim.server.service;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.chat.notify.P2pChatNotifyPacket;
import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.chat.response.P2pChatResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;
import org.chord.sim.common.util.SIMUtils;
import org.chord.sim.rpc.common.URL;
import org.chord.sim.rpc.common.asyn.RpcFuture;
import org.chord.sim.rpc.proxy.RpcProxyFactory;
import org.chord.sim.server.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author chord
 * date 2022/2/2 21:58
 * function:
 */
@Service
public class ChatService implements org.chord.sim.server.api.ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public P2pChatResponsePacket P2pChat(P2pChatRequestPacket requestPacket) throws ExecutionException, InterruptedException {

        P2pChatResponsePacket responsePacket = null;
        Message message = null;

        String fromUserId = requestPacket.getFromUserId();
        String toUserId = requestPacket.getToUserId();

        // 获取目标用户的channel
        NioSocketChannel channel = sessionService.getChannel(toUserId);
        if (channel == null) {
            if (userService.getUserById(toUserId) == null) {
                responsePacket = new P2pChatResponsePacket();
                responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());
                responsePacket.setStatus(Status.FAILED);
                responsePacket.setMsg("目标用户不存在，发送失败");
                return responsePacket;
            }
            // channel为null，user存在，说明目标用户可能不在线，也可能连接在另一个服务器上
            String remoteAddress = sessionService.getServerAddress(toUserId);
            if (remoteAddress != null) {
                // 目标用户连接在另一个服务器上
                // 此时使用RPC进行远程调用
                String[] split = remoteAddress.split(":");
                String remoteHost = split[0];
                Integer remotePort = Integer.parseInt(split[1]);
                URL remoteUrl = new URL(remoteHost, remotePort);
                // 获取代理类
                org.chord.sim.server.api.ChatService chatService = RpcProxyFactory.getProxy(
                        org.chord.sim.server.api.ChatService.class, remoteUrl);
                // 异步调用
                Future future = (RpcFuture) chatService.P2pChat(requestPacket);
                // 获取返回值
                responsePacket = (P2pChatResponsePacket) future.get();
                // 这里直接将响应包返回，不必执行将消息存入数据库的过程，因为这些操作在rpc的服务端已经进行过了
                return responsePacket;

            }
        } else {
            // 创建消息对象
            String msgId = SIMUtils.generateUUID();
            message = new Message();
            message.setFromId(fromUserId);
            message.setToId(toUserId);
            message.setMsgId(msgId);
            message.setSeqNumber(String.valueOf(requestPacket.getSeqNumber()));
            message.setMsgType(0);
            message.setStatus(0);
            message.setMsgContent(requestPacket.getMsg());

            // 创建消息通知包
            P2pChatNotifyPacket notifyPacket = new P2pChatNotifyPacket();

            org.chord.sim.common.pojo.Message pojoMessage = new org.chord.sim.common.pojo.Message();
            pojoMessage.setMsgContent(requestPacket.getMsg());
            pojoMessage.setMsgId(msgId);
            pojoMessage.setFromId(fromUserId);
            pojoMessage.setFromUserName(userService.getUserNameById(fromUserId));
            pojoMessage.setSeqNumber(String.valueOf(requestPacket.getSeqNumber()));

            notifyPacket.setMessage(pojoMessage);

            // 发送消息
            channel.writeAndFlush(notifyPacket);

            LOGGER.info("p2p_chat: {} -> {}", fromUserId, toUserId);
        }

        if (message == null) {
            // 创建消息对象
            String msgId = SIMUtils.generateUUID();
            message = new Message();
            message.setFromId(fromUserId);
            message.setToId(toUserId);
            message.setMsgId(msgId);
            message.setSeqNumber(String.valueOf(requestPacket.getSeqNumber()));
            message.setMsgType(0);
            message.setStatus(0);
            message.setMsgContent(requestPacket.getMsg());
        }

        // 存储消息到mysql数据库
        if (message != null) {
            messageService.addMessage(message);
        }

        responsePacket = new P2pChatResponsePacket();
        responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());
        responsePacket.setStatus(Status.OK);
        responsePacket.setMsg("消息发送成功！");
        return responsePacket;
    }

}
