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
    private static final String PREFIX_TICKET = "login-ticket";
    private static final String PREFIX_SERVER_ADDRESS = "server-address";

    // 用于生成userId
    public static String getNextUserIdKey() {
        return KEY_NEXT_USER_ID;
    }

    // user:userId
    public static String getUserKey(String userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // login-ticket:UUID
    public static String getLogInTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // server-address:userId
    public static String getServerAddressKey(String userId) {
        return PREFIX_SERVER_ADDRESS + SPLIT + userId;
    }
}
