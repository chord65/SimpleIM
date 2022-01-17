package org.chord.sim.client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.client.handler.RegisterResponseHandler;
import org.chord.sim.client.util.SpringUtils;
import org.chord.sim.common.handler.PacketCodecHandler;
import org.chord.sim.common.handler.Splitter;
import org.chord.sim.common.pojo.User;
import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author chord
 * date 2022/1/16 23:30
 * function:
 */

@Component
public class SIMClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SIMClient.class);

    private static int MAX_RETRY = 5;

    private volatile boolean hasLogIn = false;

    @Value("${sim.router.host}")
    private String routerHost;

    @Value("${sim.router.port}")
    private int routerPort;

    @Value("${sim.server.host}")
    private String serverHost;

    @Value("${sim.server.port}")
    private int serverPort;

    private Bootstrap bootstrapToRouter;
    private Bootstrap bootstrapToServer;

    private volatile Channel channelToRouter;
    private volatile Channel channelToServer;

    private volatile User userInfo = new User();

    /**
     * 连接router服务器
     */
    @PostConstruct
    public void connectToRouter() {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrapToRouter = new Bootstrap();
        bootstrapToRouter
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        // 拆包
                        channel.pipeline().addLast(new Splitter());
                        // 编解码
                        channel.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        // 处理注册响应
                        channel.pipeline().addLast(RegisterResponseHandler.INSTANCE);
                    }
                });

        connect(bootstrapToRouter, routerHost, routerPort, MAX_RETRY);
    }

    private void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                if (bootstrap == this.bootstrapToRouter) {
                    LOGGER.info("路由服务器连接成功！");
                    this.channelToRouter = ((ChannelFuture) future).channel();
                }
                if (bootstrap == this.bootstrapToServer) {
                    LOGGER.info("服务器连接成功！");
                    this.channelToServer = ((ChannelFuture) future).channel();
                }
            } else if (retry == 0) {
                LOGGER.error("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                LOGGER.error("连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * 注册功能
     */

    public void register(String userName, String passWord) {
        RegisterRequestPacket requestPacket = new RegisterRequestPacket();
        requestPacket.setUserName(userName);
        requestPacket.setPassWord(passWord);

        this.channelToRouter.writeAndFlush(requestPacket);
    }

    public User getUserInfo() {
        return userInfo;
    }

    public boolean isLogIn() {
        return hasLogIn;
    }
}
