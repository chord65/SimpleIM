package org.chord.sim.rpc.demo.client;

import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.chat.response.P2pChatResponsePacket;
import org.chord.sim.rpc.common.URL;
import org.chord.sim.rpc.common.asyn.RpcFuture;
import org.chord.sim.rpc.proxy.RpcProxyFactory;
import org.chord.sim.server.api.ChatService;

import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author chord
 * date 2022/3/22 16:41
 * function:
 */
public class ClientDemo {

    public static void main(String[] args) {

        try {
            P2pChatRequestPacket requestPacket = new P2pChatRequestPacket();
            requestPacket.setMsg("hello");

            URL url = new URL("127.0.0.1", 10005);
            ChatService chatService = RpcProxyFactory.getProxy(ChatService.class, url);
            Future future = (RpcFuture) chatService.P2pChat(requestPacket);
            P2pChatResponsePacket response = (P2pChatResponsePacket) future.get();
            System.out.println(response.getMsg());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
