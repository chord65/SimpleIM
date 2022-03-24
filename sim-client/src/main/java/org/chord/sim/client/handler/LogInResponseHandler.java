package org.chord.sim.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.protocol.chat.response.LogInResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;

/**
 * @author chord
 * date 2022/1/27 22:24
 * function:
 */
@ChannelHandler.Sharable
public class LogInResponseHandler extends SimpleChannelInboundHandler<LogInResponsePacket> {

    public static final LogInResponseHandler INSTANCE = new LogInResponseHandler();

    private SIMClient simClient;

    private LogInResponseHandler() {
        simClient = SpringUtils.getBean(SIMClient.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogInResponsePacket responsePacket) throws Exception {

        if(responsePacket.getStatus() == Status.FAILED) {
            System.out.println("登录失败：" + responsePacket.getMsg());
            return;
        }

        String serverAddress = responsePacket.getServerAddress();
        String[] split = serverAddress.split(":");

        // 设置服务器地址
        simClient.setServerHost(split[0]);
        simClient.setServerPort(Integer.valueOf(split[1]));

        // 设置登录凭证
        simClient.setLogInTicket(responsePacket.getLogInTicket());

        // 获取用户名
        simClient.getUserInfo().setUserName(responsePacket.getUserName());

        // 设置登录状态
        simClient.setLogInStatus(true);
        System.out.println("登录成功！");
        System.out.println("服务器地址为： " + simClient.getServerHost() + ":" + simClient.getServerPort());

        System.out.println(responsePacket.getUserName());
        System.out.println(responsePacket.getLogInTicket());

        // 连接到chat服务器
        simClient.connectToServer();

        // 进行认证
        simClient.authentication();
    }
}
