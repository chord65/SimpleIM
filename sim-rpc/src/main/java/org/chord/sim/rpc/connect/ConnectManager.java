package org.chord.sim.rpc.connect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chord.sim.rpc.common.URL;
import org.chord.sim.rpc.common.handler.PacketCodecHandler;
import org.chord.sim.rpc.common.handler.Splitter;
import org.chord.sim.rpc.connect.handler.NettyAsynHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author chord
 * date 2022/3/19 23:10
 * function:
 */
public class ConnectManager {

    public static ConnectManager INSTANCE = new ConnectManager();

    private ConnectManager() {}

    // 设置一个容量为4的线程组用于所有的连接
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    // 设置一个创建新连接时的锁，防止对同一个URL并发创建连接时产生多余的连接
    private ReentrantLock creatConnectionLock = new ReentrantLock();

    // url和handler的对应关系
    private Map<URL, NettyAsynHandler> urlToHandler = new ConcurrentHashMap<>();

    public NettyAsynHandler getHandlerByUrl(URL url) throws InterruptedException {
        NettyAsynHandler handler = urlToHandler.getOrDefault(url, null);
        if (handler == null) {
            handler = connect(url);
        }
        return handler;
    }

    private NettyAsynHandler connect(URL url) throws InterruptedException {
        creatConnectionLock.lock();

        // 如果连接已经存在则直接返回
        if (urlToHandler.containsKey(url)) {
            NettyAsynHandler res = urlToHandler.get(url);
            creatConnectionLock.unlock();
            return res;
        }

        // 创建新连接
        System.out.println("RPC: 创建连接到 " + url.getHostName() + ":" + url.getPort());

        // 新建一个NettyAsynHandler
        NettyAsynHandler nettyAsynHandler = new NettyAsynHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel channel) throws Exception {
                         channel.pipeline().addLast(new Splitter());
                         channel.pipeline().addLast(PacketCodecHandler.INSTANCE);
                         channel.pipeline().addLast(nettyAsynHandler);
                     }
                 });

        // 连接到服务端(同步等待)
        bootstrap.connect(url.getHostName(), url.getPort()).sync();

        // 将handler注册到对应的url
        urlToHandler.put(url, nettyAsynHandler);

        creatConnectionLock.unlock();

        return nettyAsynHandler;
    }

}
