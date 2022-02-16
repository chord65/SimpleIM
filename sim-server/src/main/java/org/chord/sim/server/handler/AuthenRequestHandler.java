package org.chord.sim.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.request.AuthenRequestPacket;
import org.chord.sim.common.protocol.response.AuthenResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.server.server.attribute.ChannelAttributes;
import org.chord.sim.server.util.SpringUtils;
import org.chord.sim.server.service.UserService;

import javax.print.attribute.Attribute;

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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 对用户做下线操作
        Channel channel = ctx.channel();
        String userId = channel.attr(ChannelAttributes.USER_ID).get();
        userService.logout(userId);

        super.channelInactive(ctx);
    }
}
