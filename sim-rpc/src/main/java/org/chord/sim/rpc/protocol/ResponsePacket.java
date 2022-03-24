package org.chord.sim.rpc.protocol;

import org.chord.sim.rpc.protocol.constant.PacketType;
import lombok.Data;

/**
 * @author chord
 * date 2022/3/9 22:28
 * function:
 */
@Data
public class ResponsePacket extends Packet{

    private String requestId;

    private Object result;

    private String error;

    private Class resultType;

    @Override
    public Byte getPacketType() {
        return PacketType.RESPONSE;
    }
}
