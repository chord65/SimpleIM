package org.chord.sim.server.config;

import org.chord.sim.rpc.proxy.RpcProxyFactory;
import org.chord.sim.rpc.server.RpcServer;
import org.chord.sim.server.api.ChatService;
import org.chord.sim.server.util.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chord
 * date 2022/3/22 20:30
 * function:
 */

@Configuration
public class RpcConfig {

    @Value("${sim.rpc.server.port}")
    private Integer serverPort;

    @Bean
    public RpcServer rpcServer() throws InterruptedException {

        // 注册服务
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put(ChatService.class.getName(), SpringUtils.getBean(org.chord.sim.server.service.ChatService.class));

        // 初始化RPC服务器
        RpcServer server = new RpcServer(this.serverPort, serviceMap);

        // 启动服务器
        server.start();

        return server;
    }
}
