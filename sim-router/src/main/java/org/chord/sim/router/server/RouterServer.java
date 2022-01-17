package org.chord.sim.router.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.common.handler.PacketCodecHandler;
import org.chord.sim.common.handler.Splitter;
import org.chord.sim.router.handler.RegisterRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Date;


/**
 * @author chord
 * date 2022/1/12 10:48
 * function:
 */

@Component
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
                        // 处理注册请求
                        channel.pipeline().addLast(RegisterRequestHandler.INSTANCE);
                    }
                });

        bind(bootstrap, routerServerPort);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
