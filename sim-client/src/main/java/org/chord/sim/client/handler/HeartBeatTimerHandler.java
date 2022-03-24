package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.protocol.chat.request.HeartBeatRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author chord
 * date 2022/3/24 11:51
 * function:
 */
@ChannelHandler.Sharable
public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatTimerHandler.class);

    public static final HeartBeatTimerHandler INSTANCE = new HeartBeatTimerHandler();

    // 发送心跳包的间隔
    private static final int HEARTBEAT_INTERVAL = 5;

    private SIMClient simClient;

    private HeartBeatTimerHandler() {
        this.simClient = SpringUtils.getBean(SIMClient.class);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        scheduleSendHeartBeat(ctx);

        super.channelActive(ctx);
    }

    // 定时任务：按固定时间间隔发送心跳包
    private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                ctx.writeAndFlush(new HeartBeatRequestPacket());

                LOGGER.debug("发送心跳包一个");

                scheduleSendHeartBeat(ctx);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        simClient.reconnect();
    }
}
