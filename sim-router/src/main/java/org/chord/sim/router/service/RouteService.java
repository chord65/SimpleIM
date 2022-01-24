package org.chord.sim.router.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chord
 * date 2022/1/24 21:33
 * function:
 */
public interface RouteService {

    /**
     * 路由到一个服务器地址
     */
    public String getServerAddress(List<String> serverList);
}
