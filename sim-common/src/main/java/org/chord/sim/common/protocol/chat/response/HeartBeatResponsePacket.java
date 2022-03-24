package org.chord.sim.common.protocol.chat.response;

import org.chord.sim.common.protocol.chat.constant.Command;

/**
 * @author chord
 * date 2022/3/24 11:42
 * function:
 */
public class HeartBeatResponsePacket extends BaseResponsePacket{
    @Override
    public Byte getCommand() {
        return Command.HEART_BEAT;
    }
}
