package org.chord.sim.rpc.demo.server;

import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.chat.response.P2pChatResponsePacket;
import org.chord.sim.server.api.ChatService;

/**
 * @author chord
 * date 2022/3/22 15:45
 * function:
 */
public class ChatServiceImpl implements ChatService {
    @Override
    public P2pChatResponsePacket P2pChat(P2pChatRequestPacket request) {
        P2pChatResponsePacket response = new P2pChatResponsePacket();
        response.setRequestSeqNumber(request.getSeqNumber());
        response.setMsg(request.getMsg() + " send success!");

        System.out.println("msg : " + request.getMsg());

        return response;
    }
}
