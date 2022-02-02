package org.chord.sim.server.service;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chord
 * date 2022/1/29 22:28
 * function:
 */
@Component
public class SessionService {

    @Autowired
    private RedisTemplate redisTemplate;

    private Map<String, NioSocketChannel> channelMap = new ConcurrentHashMap<>();

    public void putChannel(String userId, NioSocketChannel channel) {
        channelMap.put(userId, channel);
    }

    public NioSocketChannel getChannel(String userId) {
        return channelMap.get(userId);
    }

    public void setServerAddress(String userId, String serverAddress) {
        String redisKey = RedisKeyUtil.getServerAddressKey(userId);
        redisTemplate.opsForValue().set(userId, serverAddress);
    }

    public String getServerAddress(String userId) {
        String redisKey = RedisKeyUtil.getServerAddressKey(userId);
        return (String) redisTemplate.opsForValue().get(redisKey);
    }

}
