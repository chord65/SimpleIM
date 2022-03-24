package org.chord.sim.common.protocol.chat.codec;

import io.netty.buffer.ByteBuf;
import org.chord.sim.common.protocol.chat.Packet;

/**
 * @author chord
 * date 2022/2/19 0:06
 * function:
 */
public interface PacketCodec {

    void encode(ByteBuf byteBuf, Packet packet);
    Packet decode(ByteBuf byteBuf);
}
