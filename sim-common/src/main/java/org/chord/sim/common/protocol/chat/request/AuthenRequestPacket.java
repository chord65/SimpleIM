package org.chord.sim.common.protocol.chat.request;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

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
