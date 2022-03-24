package org.chord.sim.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.handler.IMIdleStateHandler;
import org.chord.sim.common.handler.PacketCodecHandler;
import org.chord.sim.common.handler.Splitter;
import org.chord.sim.common.util.IdleProcessPolicy;
import org.chord.sim.server.handler.*;
import org.chord.sim.server.service.SessionService;
import org.chord.sim.server.service.UserService;
import org.chord.sim.server.util.SpringUtils;
import org.chord.sim.server.util.ZkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class SIMServer {

    static final Logger LOGGER = LoggerFactory.getLogger(SIMServer.class);

    @Autowired
    private ZkUtil zkUtil;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${sim.server.port}")
    private int serverPort;

    /**
     * 启动server
     */
    @PostConstruct
    public void start() throws UnknownHostException {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        // 空闲检测
                        channel.pipeline().addLast(new IMIdleStateHandler(SpringUtils.getBean(IdleProcessPolicy.class)));
                        // 拆包
                        channel.pipeline().addLast(new Splitter());
                        // 编解码
                        channel.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        // 处理认证请求
                        channel.pipeline().addLast(AuthenRequestHandler.INSTANCE);
                        // 处理心跳求情
                        channel.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);
                        // 处理单聊请求
                        channel.pipeline().addLast(P2pChatRequestHandler.INSTANCE);
                        // 处理离线消息拉取请求
                        channel.pipeline().addLast(PullMessagesRequestHandler.INSTANCE);
                        // 处理消息接收响应
                        channel.pipeline().addLast(MessageReceiveResponseHandler.INSTANCE);
                    }
                });

        bootstrap.bind(serverPort).addListener(future -> {
            if (future.isSuccess()) {
                LOGGER.info("端口[" + serverPort + "]绑定成功!");
                // 注册到zookeeper
                zkUtil.registerServerNode(Inet4Address.getLocalHost().getHostAddress(), serverPort);
            } else {
                LOGGER.info("端口[" + serverPort + "]绑定失败!");
            }
        });

    }

    /**
     * 服务器停机前释放连接
     */
    @PreDestroy
    public void shutDown() {

        LOGGER.info("shutdown gracefully");

        // 删除zookeeper节点
        zkUtil.delServerNode();
        // 断开zookeeper连接
        zkUtil.disconnect();

        // 对所有连接的用户执行下线操作
        List<String> userIdList = sessionService.getUserIdList();
        for (String userId : userIdList) {
            userService.logout(userId);
        }

    }

}
