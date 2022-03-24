package org.chord.sim.rpc.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.rpc.common.handler.PacketCodecHandler;
import org.chord.sim.rpc.common.handler.Splitter;
import org.chord.sim.rpc.server.handler.RpcServerHandler;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chord
 * date 2022/3/21 20:25
 * function:
 */
public class RpcServer {

    // 存放服务接口的名字和对应的实现类的对象
    private Map<String, Object> serviceMap;

    // 执行反射调用的线程池
    private ExecutorService taskService = new ThreadPoolExecutor(
            10, 10, 600L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    // 存放要绑定的端口号
    private Integer serverPort;

    // netty服务器所需的两个EventLoopGroup
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    // 所有channel共用的RpcServerHandler
    //    private RpcServerHandler rpcServerHandler;


    public RpcServer(Integer serverPort, Map<String, Object> serviceMap) {
        this.serverPort = serverPort;
        this.serviceMap = serviceMap;
//        this.rpcServerHandler = new RpcServerHandler(serviceMap, taskService);
    }

    public void start() throws InterruptedException {
        // 创建并初始化netty服务器的引导类
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new Splitter());
                        channel.pipeline().addLast(PacketCodecHandler.INSTANCE);
                        channel.pipeline().addLast(new RpcServerHandler(serviceMap, taskService));
                    }
                });

        bootstrap.bind(this.serverPort).sync();

        System.out.printf("RPC : 服务器绑定在端口[%d]\n", this.serverPort);

    }
}
