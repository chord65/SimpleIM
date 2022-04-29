package org.chord.sim.router.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.data.Stat;
import org.chord.sim.router.cache.ServerListCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chord
 * date 2022/1/19 0:00
 * function:
 */
@Component
public class ZkUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkUtil.class);

    @Value("${sim.zk.root-path}")
    private String ZK_ROOT_PATH;

    @Value("${sim.zk.server-path}")
    private String ZK_SERVER_PATH;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private ServerListCache serverListCache;

    /**
     * 注册对子节点的监听器
     */
    public void subscribeWatcher() throws Exception {
        String serverPath = ZK_ROOT_PATH + ZK_SERVER_PATH;

        // 判断要监听的节点是否存在，不存在则先创建
        Stat status = zkClient.checkExists().forPath(serverPath);
        if (status == null) {
            zkClient.create().creatingParentContainersIfNeeded().forPath(serverPath, "".getBytes());
        }

        // 注册监听器
        // 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
        //ExecutorService pool = Executors.newFixedThreadPool(2);


        // 监听子节点变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, serverPath, true);
        pathChildrenCache.getListenable().addListener(
                new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        serverListCache.updateCache(zkClient.getChildren().forPath(serverPath));
                    }
                }
        );
        pathChildrenCache.start();
    }

    /**
     * 获取所有服务器节点
     */
    public List<String> getAllNode() throws Exception {
        String serverPath = ZK_ROOT_PATH + ZK_SERVER_PATH;
        return zkClient.getChildren().forPath(serverPath);
    }
}
