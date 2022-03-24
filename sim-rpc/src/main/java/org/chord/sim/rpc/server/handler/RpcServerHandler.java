package org.chord.sim.rpc.server.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.chord.sim.common.serializer.SerializeAlgorithm;
import org.chord.sim.rpc.protocol.RequestPacket;
import org.chord.sim.rpc.protocol.ResponsePacket;
import org.chord.sim.rpc.protocol.serializer.Serializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author chord
 * date 2022/3/21 23:00
 * function:
 */

public class RpcServerHandler extends SimpleChannelInboundHandler<RequestPacket> {

    // 执行反射调用使用的线程池
    private ExecutorService taskService;

    private Map<String, Object> serviceMap;

    public RpcServerHandler(Map<String, Object> serviceMap, ExecutorService taskService) {
        this.serviceMap = serviceMap;
        this.taskService = taskService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestPacket request) throws Exception {

        System.out.println("RPC : receive packet " + request.getRequestId());

        // 交给线程池去执行
        taskService.submit(()->{

            // 创建并初始化 RPC 响应对象
            ResponsePacket response = new ResponsePacket();
            response.setRequestId(request.getRequestId());
            try {

                // 获取服务对象
                String serviceName = request.getInterfaceName();
                if (serviceName == null) {
                    throw new RuntimeException("interfaceName is null!");
                }

                Object serviceBean = serviceMap.get(serviceName);

                if (serviceBean == null) {
                    throw new RuntimeException(String.format("can not find service bean by key: %s", serviceName));
                }

                // 获取反射调用所需的参数
                Class<?> serviceClass = serviceBean.getClass();
                String methodName = request.getMethodName();
                Class<?>[] parameterTypes = request.getParamsTypes();
                Object[] parameters = request.getParams();

                // 将参数对象由JSONObject类型转为对应的Java对象
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = JSONObject.toJavaObject( (JSONObject) parameters[i], parameterTypes[i]);
                }

                // System.out.println(serviceClass.getName());
                // System.out.println(methodName);
                // System.out.println("paramTypes : ");
                // for (Class clazz : parameterTypes) {
                //     System.out.print(clazz.getName() + " ");
                // }
                // System.out.println();
                // System.out.println("params : ");
                // for (Object obj : parameters) {
                //     System.out.println(obj.getClass() + " : " + obj);
                // }
                // System.out.println();

                // 执行反射调用
                Object result = null;
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                try {
                    result = method.invoke(serviceBean, parameters);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // 结果写入response
                response.setResult(result);

                // 写入返回值的类型
                response.setResultType(method.getReturnType());

            } catch (Exception e) {
                response.setError(e.getClass().toString() + " : " + e.getMessage());
            }
            // 写入 RPC 响应对象
            ctx.writeAndFlush(response);
        });

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RPC : receive from " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.printf("RPC : client %s disconnect\n", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
}
