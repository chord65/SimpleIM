package org.chord.sim.client.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.client.handler.*;
import org.chord.sim.common.handler.PacketCodecHandler;
import org.chord.sim.common.handler.Splitter;
import org.chord.sim.common.pojo.User;
import org.chord.sim.common.protocol.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    private String serverHost;
    private int serverPort;

    private Bootstrap bootstrapToRouter;
    private Bootstrap bootstrapToServer;

    private volatile Channel channelToRouter;
    private volatile Channel channelToServer;

    private volatile User userInfo = new User();

    private volatile String logInTicket;

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
                        // 处理登录响应
                        channel.pipeline().addLast(LogInResponseHandler.INSTANCE);
                    }
                });

        connect(bootstrapToRouter, routerHost, routerPort, MAX_RETRY);
    }

    public void connectToServer() {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrapToServer = new Bootstrap();
        bootstrapToServer
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
                        // 处理认证响应
                        channel.pipeline().addLast(AuthenResponseHandler.INSTANCE);
                        // 处理单聊响应
                        channel.pipeline().addLast(P2pChatResponseHandler.INSTANCE);
                        // 处理单聊消息通知
                        channel.pipeline().addLast(P2pChatNotifyHandler.INSTANCE);
                        // 处理消息拉取响应
                        channel.pipeline().addLast(PullMessagesResponseHandler.INSTANCE);
                    }
                });

        connect(bootstrapToServer, serverHost, serverPort, MAX_RETRY);
    }

    private void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                if (bootstrap == this.bootstrapToRouter) {
                    this.channelToRouter = ((ChannelFuture) future).channel();
                    LOGGER.info("路由服务器连接成功！");
                }
                if (bootstrap == this.bootstrapToServer) {
                    this.channelToServer = ((ChannelFuture) future).channel();
                    LOGGER.info("服务器连接成功！");
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

    /**
     * 登录功能
     */
    public void logIn(String userId, String password) {
        LogInRequestPacket requestPacket = new LogInRequestPacket();
        requestPacket.setUserId(userId);
        requestPacket.setPassWord(password);

        this.channelToRouter.writeAndFlush(requestPacket);
    }

    /**
     * 认证功能
     */
    public void authentication() throws InterruptedException {

        while (channelToServer == null) Thread.sleep(10);

        AuthenRequestPacket requestPacket = new AuthenRequestPacket();
        requestPacket.setLogInTicket(logInTicket);

        this.channelToServer.writeAndFlush(requestPacket);

        LOGGER.info("发送认证请求。。。");
    }

    /**
     * 单聊功能
     */
    public void p2pChat(String toUserId, String msg) {

        P2pChatRequestPacket requestPacket = new P2pChatRequestPacket();
        requestPacket.setFromUserId(this.userInfo.getUserId());
        requestPacket.setToUserId(toUserId);
        requestPacket.setMsg(msg);

        this.channelToServer.writeAndFlush(requestPacket);
    }

    /**
     * 拉取离线消息
     */
    public void PullMessages() {

        PullMessagesRequestPacket requestPacket = new PullMessagesRequestPacket();
        requestPacket.setToUserId(userInfo.getUserId());

        channelToServer.writeAndFlush(requestPacket);
    }

    @PreDestroy
    public void shutdown() {
        LOGGER.info("shutdown gracefully");

        if (channelToServer != null) {
            channelToServer.close();
        }
        if (channelToRouter != null) {
            channelToRouter.close();
        }

    }

    public User getUserInfo() {
        return userInfo;
    }

    public boolean isLogIn() {
        return hasLogIn;
    }

    public void setLogInStatus(boolean hasLogIn) {
        this.hasLogIn = hasLogIn;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getLogInTicket() {
        return logInTicket;
    }

    public void setLogInTicket(String logInTicket) {
        this.logInTicket = logInTicket;
    }
}
