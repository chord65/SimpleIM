package org.chord.sim.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.common.protocol.request.P2pChatRequestPacket;
import org.chord.sim.common.protocol.response.P2pChatResponsePacket;
import org.chord.sim.server.service.ChatService;
import org.chord.sim.server.util.SpringUtils;

/**
 * @author chord
 * date 2022/2/2 21:54
 * function:
 */
@ChannelHandler.Sharable
public class P2pChatRequestHandler extends SimpleChannelInboundHandler<P2pChatRequestPacket> {

    public static final P2pChatRequestHandler INSTANCE = new P2pChatRequestHandler();

    private ChatService chatService;

    private P2pChatRequestHandler() {
        chatService = SpringUtils.getBean(ChatService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, P2pChatRequestPacket requestPacket) throws Exception {
        P2pChatResponsePacket responsePacket = chatService.P2pChat(requestPacket);
        ctx.writeAndFlush(responsePacket);
    }
}
