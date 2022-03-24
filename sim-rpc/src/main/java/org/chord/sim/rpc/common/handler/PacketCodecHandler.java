package org.chord.sim.rpc.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.chord.sim.rpc.protocol.Packet;
import org.chord.sim.rpc.protocol.codec.RpcPacketCodec;

import java.util.List;

/**
 * @author chord
 * date 2022/1/12 11:55
 * function:
 */

@ChannelHandler.Sharable
public class PacketCodecHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final PacketCodecHandler INSTANCE = new PacketCodecHandler();

    private RpcPacketCodec packetCodec = RpcPacketCodec.INSTANCE;

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
