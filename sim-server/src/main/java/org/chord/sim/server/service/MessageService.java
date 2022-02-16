package org.chord.sim.server.service;

import org.chord.sim.server.dao.MessageMapper;
import org.chord.sim.server.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chord
 * date 2022/2/9 17:51
 * function:
 */

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> findMessageByToId(String toId) {
        return messageMapper.selectMessageByToId(toId);
    }

    public void addMessage(Message msg) {
        messageMapper.insertMessage(msg);
    }

    public void updateStatus(String msgId, int status) {
        messageMapper.updateStatus(msgId, status);
    }

    public List<Message> findOfflineMessageByToId(String toId) {
        return messageMapper.selectOfflineMessageByToId(toId);
    }
}
