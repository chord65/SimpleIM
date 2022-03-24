package org.chord.sim.rpc.protocol;

import org.chord.sim.rpc.protocol.constant.PacketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chord
 * date 2022/2/19 22:03
 * function:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPacket extends Packet{

    private String requestId;

    private String interfaceName;

    private String methodName;

    private Object[] params;
    //防止重载
    private Class[] paramsTypes;
    //是否异步
    //    private int mode;

    @Override
    public Byte getPacketType() {
        return PacketType.REQUEST;
    }
}
