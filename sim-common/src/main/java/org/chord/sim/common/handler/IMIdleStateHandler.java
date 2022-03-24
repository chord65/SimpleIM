package org.chord.sim.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.chord.sim.common.util.IdleProcessPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author chord
 * date 2022/3/24 10:59
 * function:
 */

public class IMIdleStateHandler extends IdleStateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMIdleStateHandler.class);

    public static final int READER_IDLE_TIME = 15;

    private IdleProcessPolicy idleProcessPolicy;

    public IMIdleStateHandler(IdleProcessPolicy idleProcessPolicy) {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
        this.idleProcessPolicy = idleProcessPolicy;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {

        idleProcessPolicy.process(ctx, this);
    }

}
