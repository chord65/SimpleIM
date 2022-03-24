package org.chord.sim.common.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.chord.sim.common.handler.IMIdleStateHandler;

/**
 * @author chord
 * date 2022/3/24 17:29
 * function:
 */
public interface IdleProcessPolicy {

    public void process(ChannelHandlerContext ctx, IMIdleStateHandler handler);
}
