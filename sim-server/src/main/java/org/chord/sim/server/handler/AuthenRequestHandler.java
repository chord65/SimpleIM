package org.chord.sim.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.request.AuthenRequestPacket;
import org.chord.sim.common.protocol.response.AuthenResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.server.Util.SpringUtils;
import org.chord.sim.server.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author chord
 * date 2022/1/28 23:12
 * function:
 */
@ChannelHandler.Sharable
public class AuthenRequestHandler extends SimpleChannelInboundHandler<AuthenRequestPacket> {

    public static final AuthenRequestHandler INSTANCE = new AuthenRequestHandler();

    private UserService userService;

    private AuthenRequestHandler() {
        userService = SpringUtils.getBean(UserService.class);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthenRequestPacket requestPacket) throws Exception {

        AuthenResponsePacket responsePacket = userService.authentication(requestPacket, (NioSocketChannel) ctx.channel());
        ctx.writeAndFlush(responsePacket);
        // 判断是否认证成功，如果失败，关闭channel
        if (responsePacket.getStatus() == Status.FAILED) {
            ctx.channel().close();
        }
    }
}
