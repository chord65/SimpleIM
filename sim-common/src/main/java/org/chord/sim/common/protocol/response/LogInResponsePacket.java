package org.chord.sim.common.protocol.response;

import org.chord.sim.common.protocol.constant.Command;

/**
 * @author chord
 * date 2022/1/27 21:27
 * function:
 */
public class LogInResponsePacket extends BaseResponsePacket{

    private String serverAddress;
    private String userName;
    private String logInTicket;

    @Override
    public Byte getCommand() {
        return Command.LOGIN;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogInTicket() {
        return logInTicket;
    }

    public void setLogInTicket(String logInTicket) {
        this.logInTicket = logInTicket;
    }
}
