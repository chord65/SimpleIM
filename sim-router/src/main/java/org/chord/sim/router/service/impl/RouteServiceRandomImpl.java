package org.chord.sim.router.service.impl;

import org.chord.sim.router.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chord
 * date 2022/1/25 0:13
 * function:
 */
@Service
public class RouteServiceRandomImpl implements RouteService {

    @Override
    public String getServerAddress(List<String> serverList) {
        int size = serverList.size();
        if (size <= 0) {
            return null;
        }
        int offset = ThreadLocalRandom.current().nextInt(size);

        return serverList.get(offset);
    }
}
