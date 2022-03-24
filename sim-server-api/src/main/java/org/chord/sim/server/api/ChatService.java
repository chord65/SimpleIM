package org.chord.sim.server.api;

import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.chat.response.P2pChatResponsePacket;

import java.util.concurrent.ExecutionException;

/**
 * @author chord
 * date 2022/3/21 20:45
 * function:
 */
public interface ChatService {

    public Object P2pChat(P2pChatRequestPacket requestPacket) throws ExecutionException, InterruptedException;
}
