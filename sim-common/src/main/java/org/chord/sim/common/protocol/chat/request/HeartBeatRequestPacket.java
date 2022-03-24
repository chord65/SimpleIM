package org.chord.sim.common.protocol.chat.request;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/3/24 11:40
 * function:
 */
public class HeartBeatRequestPacket extends Packet {
    @Override
    public Byte getCommand() {
        return Command.HEART_BEAT;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }
}
