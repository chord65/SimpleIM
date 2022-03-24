package org.chord.sim.rpc.demo.server;

import org.chord.sim.rpc.server.RpcServer;
import org.chord.sim.server.api.ChatService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chord
 * date 2022/3/22 16:22
 * function:
 */
public class RpcBootStrap {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put(ChatService.class.getName(), new ChatServiceImpl());
        RpcServer rpcServer = new RpcServer(10005, serviceMap);
        rpcServer.start();
    }
}
