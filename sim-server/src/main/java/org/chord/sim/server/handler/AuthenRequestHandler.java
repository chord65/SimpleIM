package org.chord.sim.server.handler;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.protocol.chat.request.AuthenRequestPacket;
import org.chord.sim.common.protocol.chat.request.HeartBeatRequestPacket;
import org.chord.sim.common.protocol.chat.response.AuthenResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;
import org.chord.sim.server.server.attribute.ChannelAttributes;
import org.chord.sim.server.util.SpringUtils;
import org.chord.sim.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author chord
 * date 2022/1/28 23:12
 * function:
 */
@ChannelHandler.Sharable
public class AuthenRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenRequestHandler.class);

    public static final AuthenRequestHandler INSTANCE = new AuthenRequestHandler();

    private UserService userService;

    private AuthenRequestHandler() {
        userService = SpringUtils.getBean(UserService.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthenRequestPacket) {

            AuthenRequestPacket requestPacket = (AuthenRequestPacket) msg;
            AuthenResponsePacket responsePacket = userService.authentication(requestPacket, (NioSocketChannel) ctx.channel());
            ctx.writeAndFlush(responsePacket);
            // 判断是否认证成功，如果失败，关闭channel
            if (responsePacket.getStatus() == Status.FAILED) {
                ctx.channel().close();
            }
            else {
                // 认证成功则移除该handler
                ctx.pipeline().remove(this);
            }
        }
        else if (msg instanceof HeartBeatRequestPacket){

            ctx.fireChannelRead(msg);
        }
        else {
            // 未认证成功时收到除认证请求和心跳包之外的请求包，直接关闭连接
            ctx.channel().close();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().attr(ChannelAttributes.USER_ID).get();
        LOGGER.info("user {} authenticate successfully, authentication handler removed!", userId);
    }

    // @Override
    // protected void channelRead0(ChannelHandlerContext ctx, AuthenRequestPacket requestPacket) throws Exception {
    //
    //     AuthenResponsePacket responsePacket = userService.authentication(requestPacket, (NioSocketChannel) ctx.channel());
    //     ctx.writeAndFlush(responsePacket);
    //     // 判断是否认证成功，如果失败，关闭channel
    //     if (responsePacket.getStatus() == Status.FAILED) {
    //         ctx.channel().close();
    //     }
    // }

}
