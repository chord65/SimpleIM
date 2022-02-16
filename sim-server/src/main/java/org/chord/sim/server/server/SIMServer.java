package org.chord.sim.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.handler.PacketCodecHandler;
import org.chord.sim.common.handler.Splitter;
import org.chord.sim.server.handler.P2pChatRequestHandler;
import org.chord.sim.server.handler.PullMessagesRequestHandler;
import org.chord.sim.server.util.ZkUtil;
import org.chord.sim.server.handler.AuthenRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@Component
public class SIMServer {

    static final Logger LOGGER = LoggerFactory.getLogger(SIMServer.class);

    @Autowired
    private ZkUtil zkUtil;

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
                        // 拆包
                        channel.pipeline().addLast(new Splitter());
                        // 编解码
                        channel.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        // 处理认证请求
                        channel.pipeline().addLast(AuthenRequestHandler.INSTANCE);
                        // 处理单聊请求
                        channel.pipeline().addLast(P2pChatRequestHandler.INSTANCE);
                        // 处理离线消息拉取请求
                        channel.pipeline().addLast(PullMessagesRequestHandler.INSTANCE);
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

    }

}
