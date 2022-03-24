package org.chord.sim.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.chat.request.HeartBeatRequestPacket;
import org.chord.sim.common.protocol.chat.response.HeartBeatResponsePacket;
import org.chord.sim.server.server.attribute.ChannelAttributes;
import org.chord.sim.server.service.UserService;
import org.chord.sim.server.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chord
 * date 2022/3/24 17:48
 * function:
 */
@ChannelHandler.Sharable
public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<HeartBeatRequestPacket> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatRequestHandler.class);

    public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

    private UserService userService;

    private HeartBeatRequestHandler() {
        this.userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket heartBeatRequestPacket) throws Exception {

        // 返回一个心跳包
        ctx.channel().writeAndFlush(new HeartBeatResponsePacket());

        LOGGER.debug("返回一个来自[{}]的心跳包", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 对用户做下线操作
        Channel channel = ctx.channel();
        String userId = channel.attr(ChannelAttributes.USER_ID).get();
        if (userId != null) {
            userService.logout(userId);
        }

        // 关闭连接
        ctx.channel().close();

        super.channelInactive(ctx);
    }
}
