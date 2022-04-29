package org.chord.sim.router.service.impl;

import org.chord.sim.router.service.RouteService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chord
 * date 2022/4/29 10:58
 * function:
 */
public class RouterServiceRoundRobinImpl implements RouteService {

    private AtomicInteger roundIdx = new AtomicInteger(0);

    @Override
    public String getServerAddress(List<String> serverList) {
        int n = serverList.size();
        if (n <= 0) {
            return null;
        }
        int idx = roundIdx.accumulateAndGet(n, (x, y) -> (x + 1) % y);
        return serverList.get(idx);
    }
}
