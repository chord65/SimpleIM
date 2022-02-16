package org.chord.sim.server.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author chord
 * date 2022/1/18 22:02
 * function:
 */

@Component
public class ZkUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkUtil.class);

    @Value("${sim.zk.host}")
    private String ZK_HOST;

    @Value("${sim.zk.port}")
    private String ZK_PORT;

    @Value("${sim.zk.root-path}")
    private String ZK_ROOT_PATH;

    @Value("${sim.zk.server-path}")
    private String ZK_SERVER_PATH;

    private static ZooKeeper zk;

    private String node;

    /**
     * 连接zk，并创建相应的节点
     */
    @PostConstruct
    public void connect() throws Exception {

        zk = new ZooKeeper(ZK_HOST + ":" + ZK_PORT, 10000, new Watcher() {
            // 监控所有被触发的事件
            public void process(WatchedEvent event) {

            }
        });
        Stat zkPath = null;
        zkPath = zk.exists(ZK_ROOT_PATH, false);
        if (zkPath == null) {
            zk.create(ZK_ROOT_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        Stat serverPath = zk.exists(ZK_ROOT_PATH + ZK_SERVER_PATH, false);
        if (serverPath == null) {
            zk.create(ZK_ROOT_PATH + ZK_SERVER_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    /**
     * 关闭zk连接
     */
    public void disconnect() {
        if (zk != null) {
            try {
                zk.close();
                LOGGER.info("Zookeeper : zkClient close!");
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 向zk /sim/server注册一个Server节点
     *
     * @param host
     * @param port
     */
    public void registerServerNode(String host, Integer port) {
        node = host + ":" + port;
        String nodePath = ZK_ROOT_PATH + ZK_SERVER_PATH + "/" + node;
        try {
            zk.create(nodePath, node.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            LOGGER.info("Zookeeper : add zNode : " + nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除server注册的节点
     */
    public void delServerNode(){
        String nodePath = ZK_ROOT_PATH + ZK_SERVER_PATH + "/" + node;
        try {
            zk.delete(nodePath,0);
            LOGGER.info("Zookeeper : delete zNode : " + nodePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
