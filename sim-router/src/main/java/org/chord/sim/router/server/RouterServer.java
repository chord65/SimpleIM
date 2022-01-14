package org.chord.sim.router.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.handler.Spliter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;


/**
 * @author chord
 * date 2022/1/12 10:48
 * function:
 */
public class RouterServer {

    static final Logger LOGGER = LoggerFactory.getLogger(RouterServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Value("${router-server.port}")
    private int routerServerPort;

    /**
     * 启动server
     */
    @PostConstruct
    public void start() {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .localAddress(new InetSocketAddress(routerServerPort))
                // 保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new Spliter());
                    }
                });
    }
}
