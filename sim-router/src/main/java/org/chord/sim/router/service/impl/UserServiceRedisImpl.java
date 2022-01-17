package org.chord.sim.router.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.chord.sim.common.pojo.User;
import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.common.util.RedisKeyUtil;
import org.chord.sim.common.util.SIMUtils;
import org.chord.sim.router.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;

/**
 * @author chord
 * date 2022/1/15 0:34
 * function:
 */

@Service
public class UserServiceRedisImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceRedisImpl.class);

    private static final Long FIRST_USER_ID = 100000L;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    private void init() {
        // 判断初始用户ID是否在Redis中存在，如果不存在则创建
        String nextUserIdKey = RedisKeyUtil.getNextUserIdKey();
        Long id = getLongFromRedis(nextUserIdKey);
        if (id == null) {
            redisTemplate.opsForValue().set(nextUserIdKey, FIRST_USER_ID);
        }
    }

    // 由于Long类型经过json序列化再从redis中取出并反序列化后，可能会变为Integer，所以要针对性处理一下
    private Long getLongFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) return null;
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        return (Long) obj;
    }

    private Long getLongFromRedisIncr(String key) {
        Object obj = redisTemplate.opsForValue().increment(key);
        if (obj == null) return null;
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        return (Long) obj;
    }

    @Override
    public RegisterResponsePacket register(RegisterRequestPacket requestPacket) throws Exception {

        RegisterResponsePacket responsePacket = new RegisterResponsePacket();
        responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());

        // 检查用户名、密码是否为空
        String userName = requestPacket.getUserName();
        String passWord = requestPacket.getPassWord();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(passWord)) {
            LOGGER.info("用户名/密码为空");
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("用户名/密码不能为空");
            return responsePacket;
        }

        // 生成新的userId
        String nextUserIdKey = RedisKeyUtil.getNextUserIdKey();
        Long userId = getLongFromRedisIncr(nextUserIdKey);
        if (userId == null) {
            LOGGER.error("userID 生成失败！");
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("userId 获取失败！");
            return responsePacket;
        }

        // 创建用户
        User user = new User();
        user.setUserId(userId.toString());
        user.setUserName(requestPacket.getUserName());

        // 设置密码,MD5加密
        user.setPassWord(SIMUtils.md5(passWord));

        // 存入Redis
        String userKey = RedisKeyUtil.getUserKey(user.getUserId());
        redisTemplate.opsForValue().set(userKey, user);

        LOGGER.info(userName + " 注册成功！ ID : " + userId);

        // 设置响应信息
        responsePacket.setStatus(Status.OK);
        responsePacket.setUserId(userId.toString());

        return responsePacket;
    }


}