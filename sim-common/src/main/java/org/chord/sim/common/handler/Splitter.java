package org.chord.sim.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.chord.sim.common.protocol.PacketCodec;

/**
 * @author chord
 * date 2022/1/12 11:09
 * function:
 */
public class Splitter extends LengthFieldBasedFrameDecoder {

    private static final int LENGTH_FIELD_OFFSET = 16;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public Splitter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (PacketCodec.getMagicNumber(in) != PacketCodec.MAGIC_NUMBER) {
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
