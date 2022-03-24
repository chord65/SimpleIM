package org.chord.sim.router.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.chat.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.chat.response.RegisterResponsePacket;
import org.chord.sim.router.service.UserService;
import org.chord.sim.router.util.SpringUtils;

/**
 * @author chord
 * date 2022/1/17 19:10
 * function:
 */
@ChannelHandler.Sharable
public class RegisterRequestHandler extends SimpleChannelInboundHandler<RegisterRequestPacket> {

    public static final RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    private UserService userService;

    private RegisterRequestHandler() {

        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterRequestPacket requestPacket) throws Exception {
        RegisterResponsePacket responsePacket = userService.register(requestPacket);
        ctx.writeAndFlush(responsePacket);
    }
}
