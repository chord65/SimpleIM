package org.chord.sim.client.console.command;

import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;

import java.util.Scanner;

/**
 * @author chord
 * date 2022/2/2 21:05
 * function:
 */
public class P2pChatCommand implements ConsoleCommand{

    private SIMClient simClient = SpringUtils.getBean(SIMClient.class);

    @Override
    public void exec(Scanner scanner) {
        String toUserId;
        String msg;

        if (!simClient.isLogIn()) {
            System.out.println("请登录！");
            return;
        }

        System.out.println("输入收信方ID：");
        toUserId = scanner.nextLine();
        System.out.println("输入信息：");
        msg = scanner.nextLine();

        simClient.p2pChat(toUserId, msg);
        System.out.println("消息发送中...");
    }
}
