package org.chord.sim.common.protocol.response;

import org.chord.sim.common.protocol.constant.Command;

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
