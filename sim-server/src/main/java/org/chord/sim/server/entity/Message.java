package org.chord.sim.server.entity;

import lombok.Data;
import org.springframework.data.relational.core.sql.In;

/**
 * @author chord
 * date 2022/2/5 9:37
 * function:
 */

@Data
public class Message {

    private Integer id;
    private String fromId;
    private String toId;
    private String msgId;
    private String seqNumber;
    private Integer msgType;
    private Integer status;
    private String msgContent;

}
