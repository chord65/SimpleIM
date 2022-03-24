package org.chord.sim.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.codec.ChatPacketCodec;
import org.chord.sim.common.protocol.chat.codec.factory.ChatPacketCodecFactory;

import java.util.List;

/**
 * @author chord
 * date 2022/1/12 11:55
 * function:
 */

@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    private ChatPacketCodec packetCodec = new ChatPacketCodecFactory().getCodec();

    private PacketCodecHandler() {

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        packetCodec.encode(byteBuf, packet);
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        out.add(packetCodec.decode(byteBuf));
    }


}
