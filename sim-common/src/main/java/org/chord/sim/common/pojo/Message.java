package org.chord.sim.common.pojo;

import lombok.Data;

/**
 * @author chord
 * date 2022/2/5 9:37
 * function:
 */

@Data
public class Message {

    private String fromId;
    private String fromUserName;
    private String msgId;
    private String seqNumber;
    private Integer msgType;
    private String msgContent;

}
