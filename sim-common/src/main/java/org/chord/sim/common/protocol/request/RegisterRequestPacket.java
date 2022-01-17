package org.chord.sim.common.protocol.request;

import lombok.Data;
import org.chord.sim.common.protocol.Packet;
import org.chord.sim.common.protocol.constant.Command;
import org.chord.sim.common.protocol.constant.MsgType;

/**
 * @author chord
 * date 2022/1/11 15:03
 * function:
 */

public class RegisterRequestPacket extends Packet {

    private String userName;
    private String passWord;

    @Override
    public Byte getCommand() {
        return Command.REGISTER;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
