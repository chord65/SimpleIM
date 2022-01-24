package org.chord.sim.router.util;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author chord
 * date 2022/1/23 22:15
 * function:
 */
public class ServerListListener implements Runnable{

    private ZkUtil zkUtil;

    public ServerListListener() {
        zkUtil = SpringUtils.getBean(ZkUtil.class);
    }

    @Override
    public void run() {
        try {
            zkUtil.subscribeWatcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
