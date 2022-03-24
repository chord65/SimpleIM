package org.chord.sim.rpc.common.exception;

/**
 * @author chord
 * date 2022/3/21 19:37
 * function:
 */
public class ServerErrorException extends RuntimeException{
    public ServerErrorException(String s) {
        super(s);
    }
}
