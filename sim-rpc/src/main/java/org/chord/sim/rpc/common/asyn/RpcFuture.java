package org.chord.sim.rpc.common.asyn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.chord.sim.rpc.common.exception.ServerErrorException;
import org.chord.sim.rpc.protocol.RequestPacket;
import org.chord.sim.rpc.protocol.ResponsePacket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author chord
 * date 2022/3/20 20:39
 * function:
 */
public class RpcFuture implements Future<Object> {

    private RequestPacket request;
    private ResponsePacket response;

    // 内置一个自定义同步器
    private Sync sync;

    public RpcFuture(RequestPacket requestPacket) {
        this.request = requestPacket;
        this.sync = new Sync();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }


    /**
     * ResponsePacket是否返回
     */
    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    /**
     * 将收到的ResponsePacket响应存入该Future对象
     * 并通知正在自旋获取该响应的线程
     */
    public void done(ResponsePacket responsePacket) {
        this.response = responsePacket;
        sync.release(1);
    }

    /**
     * 获取返回的ResponsePacket，如果没有收到该响应包，则一直自旋等待
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(1);
        if (response.getError() != null) {
            String error = response.getError();
            throw new ServerErrorException(error);
        }
        return JSONObject.toJavaObject((JSONObject) this.response.getResult(), response.getResultType());
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException();
    }

    /**
     * 自定义一个同步器，继承AQS，通过自旋改变状态
     * state有两个状态，0表示未收到服务端返回的ResponsePacket，1表示收到了
     */
    static class Sync extends AbstractQueuedSynchronizer {

        /**
         * 获取state的状态
         * 如果是0，则返回false，加入同步队列，阻塞自己，只由同步队列的头自旋获取状态
         * 当状态为1，说明收到了返回的ResponsePacket，此时可以返回true，从自旋/阻塞状态退出
         *
         */
        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == 1;
        }

        /**
         * 收到返回的ResponsePacket后，使用此方法将state置为1
         * 由于状态改变，一直在自旋的同步队列队首节点就可以从acquire()方法返回了
         */
        @Override
        protected boolean tryRelease(int arg) {
            //把状态设置为1，给tryAcquire获取锁进行操作
            return getState() != 0 || compareAndSetState(0, 1);
        }

        public boolean isDone() {
            return getState() == 1;
        }
    }
}
