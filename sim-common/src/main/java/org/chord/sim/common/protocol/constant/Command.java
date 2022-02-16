package org.chord.sim.common.protocol.constant;

/**
 * @author chord
 * date 2022/1/10 16:25
 * function:
 */
public interface Command {

    static final Byte REGISTER = 1;
    static final Byte LOGIN = 2;
    static final Byte AUTHEN = 3;
    static final Byte P2P_CHAT = 4;
    static final Byte PULL_MESSAGES = 5;

}
