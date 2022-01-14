package org.chord.sim.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.chord.sim.common.protocol.Packet;

import java.util.List;

/**
 * @author chord
 * date 2022/1/12 11:55
 * function:
 */
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, List<Object> list) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

    }
}
