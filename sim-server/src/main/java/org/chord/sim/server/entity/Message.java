package org.chord.sim.server.entity;

import lombok.Data;

/**
 * @author chord
 * date 2022/2/5 9:37
 * function:
 */

@Data
public class Message {

    private int id;
    private String fromId;
    private String toId;
    private String msgId;
    private String seqNumber;
    private int msgType;
    private int status;
    private String msgContent;

}
