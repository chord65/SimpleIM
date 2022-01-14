package org.chord.sim.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
public class SIMServer {

    static final Logger LOGGER = LoggerFactory.getLogger(SIMServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${sim.server.port}")
    private int serverPort;

    /**
     * 启动server
     */
    @PostConstruct
    public void start() {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .localAddress(new InetSocketAddress(serverPort))
                // 保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(null);
    }


}
