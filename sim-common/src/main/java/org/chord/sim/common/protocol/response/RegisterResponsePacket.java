package org.chord.sim.common.protocol.response;

import lombok.Data;
import org.chord.sim.common.protocol.Packet;
import org.chord.sim.common.protocol.constant.Command;

/**
 * @author chord
 * date 2022/1/11 15:35
 * function:
 */

public class RegisterResponsePacket extends BaseResponsePacket {


    private String userId;

    @Override
    public Byte getCommand() {
        return Command.REGISTER;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
