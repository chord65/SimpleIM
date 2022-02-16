package org.chord.sim.server.dao;

import org.apache.ibatis.annotations.Mapper;
import org.chord.sim.server.entity.Message;

import java.util.List;

/**
 * @author chord
 * date 2022/2/9 16:35
 * function:
 */

@Mapper
public interface MessageMapper {

    List<Message> selectMessageByToId(String toId);

    int insertMessage(Message message);

    int updateStatus(String msgId, int status);

    List<Message> selectOfflineMessageByToId(String toId);

}
