package org.chord.sim.common.util;


/**
 * @author chord
 * date 2022/1/15 22:52
 * function:
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String KEY_NEXT_USER_ID = "next-user-id";
    private static final String PREFIX_USER = "user";

    // 用于生成userId
    public static String getNextUserIdKey() {
        return KEY_NEXT_USER_ID;
    }

    // user:123
    public static String getUserKey(String userId) {
        return PREFIX_USER + SPLIT + userId;
    }


}
