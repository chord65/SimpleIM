package org.chord.sim.router.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.request.LogInRequestPacket;
import org.chord.sim.common.protocol.response.LogInResponsePacket;
import org.chord.sim.router.service.UserService;
import org.chord.sim.router.util.SpringUtils;

/**
 * @author chord
 * date 2022/1/27 23:27
 * function:
 */
@ChannelHandler.Sharable
public class LogInRequestHandler extends SimpleChannelInboundHandler<LogInRequestPacket> {

    public static final LogInRequestHandler INSTANCE = new LogInRequestHandler();

    private UserService userService;

    private LogInRequestHandler() {
        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogInRequestPacket requestPacket) throws Exception {
        LogInResponsePacket responsePacket = userService.logIn(requestPacket);
        ctx.channel().writeAndFlush(responsePacket);
    }
}
