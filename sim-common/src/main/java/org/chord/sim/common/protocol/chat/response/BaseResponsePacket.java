package org.chord.sim.common.protocol.chat.response;

import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.MsgType;

/**
 * @author chord
 * date 2022/1/11 16:09
 * function:
 */
public abstract class BaseResponsePacket extends Packet {

    private byte status;
    private String msg;
    private long requestSeqNumber;

    @Override
    public Byte getMsgType() {
        return MsgType.RESPONSE;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getRequestSeqNumber() {
        return requestSeqNumber;
    }

    public void setRequestSeqNumber(long requestSeqNumber) {
        this.requestSeqNumber = requestSeqNumber;
    }

}
