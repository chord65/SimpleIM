package org.chord.sim.common.protocol.chat.notify;

import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/2/2 20:39
 * function:
 */
public class P2pChatNotifyPacket extends Packet {

    private Message message;

    @Override
    public Byte getCommand() {
        return Command.P2P_CHAT;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.NOTIFY;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
