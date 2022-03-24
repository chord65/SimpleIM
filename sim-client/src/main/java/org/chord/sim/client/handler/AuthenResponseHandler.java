package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.chat.response.AuthenResponsePacket;

/**
 * @author chord
 * date 2022/1/30 0:22
 * function:
 */
@ChannelHandler.Sharable
public class AuthenResponseHandler extends SimpleChannelInboundHandler<AuthenResponsePacket> {

    public static final AuthenResponseHandler INSTANCE = new AuthenResponseHandler();

    private AuthenResponseHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthenResponsePacket responsePacket) throws Exception {
        System.out.println(responsePacket.getMsg());
    }
}
