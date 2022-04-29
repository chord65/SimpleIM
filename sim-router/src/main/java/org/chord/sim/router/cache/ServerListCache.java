package org.chord.sim.router.cache;

import com.google.common.cache.LoadingCache;
import org.chord.sim.router.util.ZkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chord
 * date 2022/1/24 16:38
 * function:
 */

@Component
public class ServerListCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListCache.class);

    @Autowired
    private ZkUtil zkUtil;

    @Autowired
    private LoadingCache<String, String> cache;

    /**
     * 更新所有缓存/先删除 再新增
     */
    public void updateCache(List<String> currentChildren) {
        cache.invalidateAll();
        StringBuffer sb = new StringBuffer("[");
        for (String currentChild : currentChildren) {
            cache.put(currentChild, currentChild);
            sb.append(currentChild);
            sb.append(",");
        }
        sb.append("]");
        LOGGER.info("server list cache updated : " + sb.toString());
    }


    /**
     * 获取所有的服务列表
     *
     * @return
     */
    public List<String> getServerList() throws Exception {

        List<String> list = new ArrayList<>();

        if (cache.size() == 0) {
            List<String> allNode = zkUtil.getAllNode();
            for (String node : allNode) {
                cache.put(node, node);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

}
