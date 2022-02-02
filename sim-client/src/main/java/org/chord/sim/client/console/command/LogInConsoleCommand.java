package org.chord.sim.client.console.command;

import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;

import java.util.Scanner;

/**
 * @author chord
 * date 2022/1/27 22:15
 * function:
 */
public class LogInConsoleCommand implements ConsoleCommand{

    private SIMClient simClient = SpringUtils.getBean(SIMClient.class);

    @Override
    public void exec(Scanner scanner) {
        String userId;
        String passWord;

        System.out.println("输入用户ID：");
        userId = scanner.next();
        System.out.println("输入密码：");
        passWord = scanner.next();

        simClient.getUserInfo().setUserId(userId);

        simClient.logIn(userId, passWord);
    }
}
