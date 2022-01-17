package org.chord.sim.client.util;

import org.chord.sim.client.client.SIMClient;

/**
 * @author chord
 * date 2022/1/17 22:21
 * function:
 */
public class SessionUtil {

    private static SIMClient simClient;

    static {
        simClient = SpringUtils.getBean(SIMClient.class);
    }

    public static boolean hasLogIn() {
        return simClient.isLogIn();
    }

}
