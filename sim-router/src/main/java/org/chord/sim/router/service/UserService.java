package org.chord.sim.router.service;

import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.response.RegisterResponsePacket;

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


}