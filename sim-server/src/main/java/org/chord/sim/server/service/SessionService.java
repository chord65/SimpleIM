package org.chord.sim.server.service;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.util.RedisKeyUtil;
import org.chord.sim.server.server.attribute.ChannelAttributes;
import org.chord.sim.server.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chord
 * date 2022/1/29 22:28
 * function:
 */
@Service
public class SessionService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 存储userId对应的channel
    private static Map<String, NioSocketChannel> channelMap = new ConcurrentHashMap<>();

    // 存储userId对应的session
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public void putChannel(String userId, NioSocketChannel channel) {
        channelMap.put(userId, channel);
    }

    public NioSocketChannel getChannel(String userId) {
        return channelMap.getOrDefault(userId, null);
    }

    public void removeChannel(String userId) {
        channelMap.remove(userId);
    }

    public void setServerAddress(String userId, String serverAddress) {
        String redisKey = RedisKeyUtil.getServerAddressKey(userId);
        redisTemplate.opsForValue().set(redisKey, serverAddress);
    }

    public String getServerAddress(String userId) {
        String redisKey = RedisKeyUtil.getServerAddressKey(userId);
        return (String) redisTemplate.opsForValue().get(redisKey);
    }

    public void removeServerAddress(String userId) {
        String redisKey = RedisKeyUtil.getServerAddressKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Session getSession(String userId) {
        return sessionMap.getOrDefault(userId, null);
    }

    public Session getSession(NioSocketChannel channel) {
        String userId = channel.attr(ChannelAttributes.USER_ID).get();
        return sessionMap.getOrDefault(userId, null);
    }

    public void bindSession(Session session, NioSocketChannel channel) {
        // 设置channel属性
        channel.attr(ChannelAttributes.USER_ID).set(session.getUserId());
        // 存入map
        sessionMap.put(session.getUserId(), session);
    }

    public void removeSession(String userId) {
        sessionMap.remove(userId);
    }

    public List<String> getUserIdList() {
        return new ArrayList<>(sessionMap.keySet());
    }
}
