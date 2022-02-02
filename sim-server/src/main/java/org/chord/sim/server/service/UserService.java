package org.chord.sim.server.service;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.request.AuthenRequestPacket;
import org.chord.sim.common.protocol.response.AuthenResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.common.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author chord
 * date 2022/1/28 23:26
 * function:
 */

@Component
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SessionService sessionService;

    @Value("${sim.server.port}")
    private String serverPort;

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

        // 向redis中存入用户连接的服务器地址
        String serverAddress = Inet4Address.getLocalHost().getHostAddress() + ":" + serverPort;
        sessionService.setServerAddress(userId, serverAddress);

        // 记录userId和对应的channel
        sessionService.putChannel(userId, channel);

        responsePacket.setStatus(Status.OK);
        responsePacket.setMsg("认证成功！");

        LOGGER.info("用户{}认证成功!", userId);

        return responsePacket;
    }

}
