package org.chord.sim.rpc.proxy;

import org.chord.sim.rpc.common.URL;
import org.chord.sim.rpc.common.asyn.RpcFuture;
import org.chord.sim.rpc.connect.ConnectManager;
import org.chord.sim.rpc.connect.handler.NettyAsynHandler;
import org.chord.sim.rpc.protocol.RequestPacket;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author chord
 * date 2022/3/21 17:02
 * function:
 */
public class RpcProxyFactory {

    public static <T> T getProxy(Class interfaceClass, URL url) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 生成requestId
                String requestId = UUID.randomUUID().toString().replace("-", "");
                // 创建请求包
                RequestPacket requestPacket = new RequestPacket(requestId, interfaceClass.getName(), method.getName(), args, method.getParameterTypes());
                // 获取url对应的NettyAsynHandler
                NettyAsynHandler handler = ConnectManager.INSTANCE.getHandlerByUrl(url);
                // 通过handler发送rpc请求包
                RpcFuture future = handler.sendRequest(requestPacket);
                // 返回future对象，用于异步获取返回值
                return future;
            }
        });
    }
}
