package org.chord.sim.server.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.chord.sim.common.handler.IMIdleStateHandler;
import org.chord.sim.common.util.IdleProcessPolicy;
import org.chord.sim.server.server.attribute.ChannelAttributes;
import org.chord.sim.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chord
 * date 2022/3/24 17:31
 * function:
 */
@Component
public class ServerIdleProcessPolicy implements IdleProcessPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerIdleProcessPolicy.class);

    @Autowired
    private UserService userService;

    @Override
    public void process(ChannelHandlerContext ctx, IMIdleStateHandler handler) {
        // 用户下线
        Channel channel = ctx.channel();
        String userId = channel.attr(ChannelAttributes.USER_ID).get();
        if (userId != null) {
            userService.logout(userId);
        }

        // 断开连接
        LOGGER.info("{} 秒内未读到数据，关闭连接[{}]", IMIdleStateHandler.READER_IDLE_TIME, ctx.channel().remoteAddress());
        ctx.channel().close();

    }
}
