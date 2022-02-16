package org.chord.sim.common.protocol.response;

import org.chord.sim.common.protocol.constant.Command;

/**
 * @author chord
 * date 2022/2/2 20:36
 * function:
 */
public class P2pChatResponsePacket extends BaseResponsePacket{

    private String msgId;

    @Override
    public Byte getCommand() {
        return Command.P2P_CHAT;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
