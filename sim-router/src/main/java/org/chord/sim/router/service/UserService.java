package org.chord.sim.router.service;

import org.chord.sim.common.protocol.chat.request.LogInRequestPacket;
import org.chord.sim.common.protocol.chat.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.chat.response.LogInResponsePacket;
import org.chord.sim.common.protocol.chat.response.RegisterResponsePacket;

/**
 * @author chord
 * date 2022/1/14 23:27
 * function:
 */

/**
 * 用户相关服务
 */

public interface UserService {

    /**
     * 注册新用户
     * @param registerRequestPacket
     * @return 返回一个用户ID
     * @throws Exception
     */
    public RegisterResponsePacket register(RegisterRequestPacket registerRequestPacket) throws Exception;

    public LogInResponsePacket logIn(LogInRequestPacket logInRequestPacket) throws Exception;

}
