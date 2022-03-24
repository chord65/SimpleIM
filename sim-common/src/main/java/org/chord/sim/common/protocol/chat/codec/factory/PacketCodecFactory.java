package org.chord.sim.common.protocol.chat.codec.factory;

import org.chord.sim.common.protocol.chat.codec.PacketCodec;

/**
 * @author chord
 * date 2022/2/19 0:00
 * function:
 */
public interface PacketCodecFactory {

    PacketCodec getCodec();
}
