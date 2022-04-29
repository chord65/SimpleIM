package org.chord.sim.router.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.chord.sim.common.pojo.User;
import org.chord.sim.common.protocol.chat.request.LogInRequestPacket;
import org.chord.sim.common.protocol.chat.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.chat.response.LogInResponsePacket;
import org.chord.sim.common.protocol.chat.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;
import org.chord.sim.common.util.RedisKeyUtil;
import org.chord.sim.common.util.SIMUtils;
import org.chord.sim.router.cache.ServerListCache;
import org.chord.sim.router.service.RouteService;
import org.chord.sim.router.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private ServerListCache serverListCache;

    @Autowired
    private RouteService routeService;

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

    @Override
    public LogInResponsePacket logIn(LogInRequestPacket requestPacket) throws Exception {

        LogInResponsePacket responsePacket = new LogInResponsePacket();
        responsePacket.setRequestSeqNumber(requestPacket.getSeqNumber());

        String userId = requestPacket.getUserId();

        // 验证账户
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = (User) redisTemplate.opsForValue().get(userKey);
        if (user == null) {
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("用户不存在！");
            return responsePacket;
        }

        // 验证密码
        String passWord = SIMUtils.md5(requestPacket.getPassWord());
        if (!passWord.equals(user.getPassWord())) {
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("密码错误！");
            return responsePacket;
        }

        // 生成登录凭证
        String ticket = SIMUtils.generateUUID();
        String ticketKey = RedisKeyUtil.getLogInTicketKey(ticket);
        redisTemplate.opsForValue().set(ticketKey, userId, 24, TimeUnit.HOURS);

        responsePacket.setLogInTicket(ticket);

        // 路由到一个chat服务器
        String serverAddress = routeService.getServerAddress(serverListCache.getServerList());

        if (serverAddress == null) {
            responsePacket.setStatus(Status.FAILED);
            responsePacket.setMsg("登录失败，没有可用的服务器！");
            return responsePacket;
        }

        responsePacket.setServerAddress(serverAddress);

        responsePacket.setStatus(Status.OK);
        responsePacket.setMsg("登录成功！");
        responsePacket.setUserName(user.getUserName());

        return responsePacket;
    }


}
