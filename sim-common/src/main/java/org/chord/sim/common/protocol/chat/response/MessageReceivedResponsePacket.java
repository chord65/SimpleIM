package org.chord.sim.common.protocol.chat.response;

import org.chord.sim.common.protocol.chat.constant.Command;

import java.util.List;

/**
 * @author chord
 * date 2022/2/17 0:32
 * function:
 */
public class MessageReceivedResponsePacket extends BaseResponsePacket{

    private String toUserId;
    private List<String> msgIdList;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RECEIVED;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public List<String> getMsgIdList() {
        return msgIdList;
    }

    public void setMsgIdList(List<String> msgIdList) {
        this.msgIdList = msgIdList;
    }
}
