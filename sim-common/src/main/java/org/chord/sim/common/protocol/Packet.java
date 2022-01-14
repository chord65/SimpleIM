package org.chord.sim.common.protocol;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

public abstract class Packet {

    /**
     * 协议版本
     */
    @JSONField(deserialize = false, serialize = false)
    private Byte version = 1;

    /**
     * 序列号
     */
    private long seqNumber;

    {
        seqNumber = System.currentTimeMillis();
    }

    @JSONField(serialize = false)
    public abstract Byte getCommand();

    @JSONField(serialize = false)
    public abstract Byte getMsgType();

    public Byte getVersion() {
        return version;
    }

    public long getSeqNumber() {
        return seqNumber;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public void setSeqNumber(long seqNumber) {
        this.seqNumber = seqNumber;
    }
}
