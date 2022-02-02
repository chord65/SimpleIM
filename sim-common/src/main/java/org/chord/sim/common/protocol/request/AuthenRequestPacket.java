package org.chord.sim.common.protocol.request;

import org.chord.sim.common.protocol.Packet;
import org.chord.sim.common.protocol.constant.Command;
import org.chord.sim.common.protocol.constant.MsgType;

/**
 * @author chord
 * date 2022/1/28 22:54
 * function:
 */
public class AuthenRequestPacket extends Packet {

    private String logInTicket;

    @Override
    public Byte getCommand() {
        return Command.AUTHEN;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }

    public String getLogInTicket() {
        return logInTicket;
    }

    public void setLogInTicket(String logInTicket) {
        this.logInTicket = logInTicket;
    }
}
