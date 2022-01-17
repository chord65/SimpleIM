package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.protocol.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chord
 * date 2022/1/16 23:53
 * function:
 */

@ChannelHandler.Sharable
public class RegisterResponseHandler extends SimpleChannelInboundHandler<RegisterResponsePacket> {

    public static final RegisterResponseHandler INSTANCE = new RegisterResponseHandler();

    private SIMClient simClient;

    private RegisterResponseHandler() {
        // 获取客户端对象
        this.simClient = SpringUtils.getBean(SIMClient.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterResponsePacket responsePacket) throws Exception {
        if (responsePacket == null) return;

        int status = responsePacket.getStatus();
        if (status == Status.FAILED) {
            System.out.println("注册失败：");
            System.out.println(responsePacket.getMsg());
        }

        // 获取返回的UserId
        String userId = responsePacket.getUserId();
        // 存储UserId
        simClient.getUserInfo().setUserId(userId);

        System.out.println("注册成功，user id 为 ：" + userId);
    }
}
