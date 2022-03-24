package org.chord.sim.common.protocol.chat.request;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/1/27 21:10
 * function:
 */
public class LogInRequestPacket extends Packet {

    private String userId;
    private String passWord;

    @Override
    public Byte getCommand() {
        return Command.LOGIN;
    }

    @Override
    public Byte getMsgType() {
        return MsgType.REQUEST;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
