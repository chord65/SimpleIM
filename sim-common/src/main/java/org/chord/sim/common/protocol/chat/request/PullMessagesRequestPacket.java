package org.chord.sim.common.protocol.chat.request;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/2/16 11:59
 * function:
 */
public class PullMessagesRequestPacket extends Packet {

    private String toUserId;

    @Override
    public Byte getCommand() {
        return Command.PULL_MESSAGES;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
