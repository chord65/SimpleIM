package org.chord.sim.common.protocol.chat.request;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/2/2 20:24
 * function:
 */
public class P2pChatRequestPacket extends Packet {

    private String fromUserId;
    private String toUserId;
    private String msg;

    @Override
    public Byte getCommand() {
        return Command.P2P_CHAT;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
