package org.chord.sim.common.protocol.chat.response;

import org.chord.sim.common.pojo.Message;
import org.chord.sim.common.protocol.chat.constant.Command;

import java.util.List;

/**
 * @author chord
 * date 2022/2/16 15:54
 * function:
 */
public class PullMessagesResponsePacket extends BaseResponsePacket{

    private List<Message> messages;

    @Override
    public Byte getCommand() {
        return Command.PULL_MESSAGES;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
