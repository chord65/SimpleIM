package org.chord.sim.server.service;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.pojo.User;
import org.chord.sim.common.protocol.chat.request.AuthenRequestPacket;
import org.chord.sim.common.protocol.chat.response.AuthenResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;
import org.chord.sim.common.util.RedisKeyUtil;
import org.chord.sim.server.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author chord
 * date 2022/1/28 23:26
 * function:
 */

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SessionService sessionService;

    @Value("${sim.server.port}")
    private String serverPort;

    @Value("${sim.rpc.server.port}")
    private String rpcPort;

    // 用户认证
    public AuthenResponsePacket authentication(AuthenRequestPacket requestPacket, NioSocketChannel channel) throws UnknownHostException {

        AuthenResponsePacket responsePacket = new AuthenResponsePacket();
        responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());

        String logInTicket = requestPacket.getLogInTicket();

        // 获取凭证，进行认证
        String ticketKey = RedisKeyUtil.getLogInTicketKey(logInTicket);
        String userId = (String) redisTemplate.opsForValue().get(ticketKey);

        if (userId == null) {
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("认证过期，请重新登录！");
            return responsePacket;
        }

        // 向redis中存入用户连接的服务器地址,端口号为RPC服务的端口号
        String serverAddress = Inet4Address.getLocalHost().getHostAddress() + ":" + rpcPort;
        sessionService.setServerAddress(userId, serverAddress);

        // 记录userId和对应的channel
        sessionService.putChannel(userId, channel);

        // 为channel设置USER_ID属性，并绑定对应的session
        Session session = new Session(userId, getUserById(userId).getUserName());
        sessionService.bindSession(session, channel);

        responsePacket.setStatus(Status.OK);
        responsePacket.setMsg("认证成功！");

        LOGGER.info("用户{}认证成功!", userId);

        return responsePacket;
    }

    public User getUserById(String userID) {
        String redisKey = RedisKeyUtil.getUserKey(userID);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    public String getUserNameById(String userID) {
        User user = getUserById(userID);
        return user.getUserName();
    }

    // 用户下线
    public void logout(String userId) {
        // 移除与channel的对应关系
        sessionService.removeChannel(userId);
        // 移除session
        sessionService.removeSession(userId);
        // 移除与服务器地址的对应关系
        sessionService.removeServerAddress(userId);

        LOGGER.info("user:{} logout", userId);
    }
}
