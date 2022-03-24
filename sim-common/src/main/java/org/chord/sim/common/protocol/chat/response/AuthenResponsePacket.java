package org.chord.sim.common.protocol.chat.response;

import org.chord.sim.common.protocol.chat.constant.Command;

/**
 * @author chord
 * date 2022/1/28 23:02
 * function:
 */
public class AuthenResponsePacket extends BaseResponsePacket{

    @Override
    public Byte getCommand() {
        return Command.AUTHEN;
    }
}
