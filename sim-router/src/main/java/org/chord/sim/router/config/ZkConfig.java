package org.chord.sim.router.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chord
 * date 2022/1/23 17:32
 * function:
 */

@Configuration
public class ZkConfig {

    @Value("${sim.zk.host}")
    private String ZK_HOST;

    @Value("${sim.zk.port}")
    private String ZK_PORT;

    @Value("${sim.zk.root-path}")
    private String ZK_ROOT_PATH;

    @Value("${sim.zk.server-path}")
    private String ZK_SERVER_PATH;

    @Bean
    public CuratorFramework zkClient() {
        String connectAddress = ZK_HOST + ":" + ZK_PORT;

        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectAddress)
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(30000)
                .build();

        // 连接到zookeeper服务器
        client.start();

        return client;
    }

}
