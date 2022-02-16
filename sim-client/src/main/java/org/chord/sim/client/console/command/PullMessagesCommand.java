package org.chord.sim.client.console.command;

import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;

import java.util.Scanner;

/**
 * @author chord
 * date 2022/2/16 23:07
 * function:
 */
public class PullMessagesCommand implements ConsoleCommand{

    private SIMClient simClient = SpringUtils.getBean(SIMClient.class);

    @Override
    public void exec(Scanner scanner) {

        simClient.PullMessages();
        System.out.println("请求发送中...");
    }
}
