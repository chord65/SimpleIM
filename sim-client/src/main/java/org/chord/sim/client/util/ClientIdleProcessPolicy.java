package org.chord.sim.client.util;

import io.netty.channel.ChannelHandlerContext;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.common.handler.IMIdleStateHandler;
import org.chord.sim.common.util.IdleProcessPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.util.PrimitiveIterator;

/**
 * @author chord
 * date 2022/3/24 17:44
 * function:
 */
@Component
public class ClientIdleProcessPolicy implements IdleProcessPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientIdleProcessPolicy.class);

    @Autowired
    private SIMClient simClient;

    @Override
    public void process(ChannelHandlerContext ctx, IMIdleStateHandler handler) {
        // 心跳包超时，关闭连接
        ctx.channel().close();

        LOGGER.info("服务器连接超时，断开连接！");
        LOGGER.info("尝试重新连接。。。");

        // 重新连接
        simClient.reconnect();
    }
}
