package org.chord.sim.common.protocol.chat.codec.factory;

import org.chord.sim.common.protocol.chat.codec.ChatPacketCodec;

/**
 * @author chord
 * date 2022/2/19 0:16
 * function:
 */
public class ChatPacketCodecFactory implements PacketCodecFactory{

    @Override
    public ChatPacketCodec getCodec() {
        return ChatPacketCodec.INSTANCE;
    }
}
