package org.chord.sim.client.console.command;

import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SpringUtils;

import java.util.Scanner;

/**
 * @author chord
 * date 2022/1/17 22:36
 * function:
 */
public class RegisterConsoleCommand implements ConsoleCommand{

    private SIMClient simClient = SpringUtils.getBean(SIMClient.class);

    @Override
    public void exec(Scanner scanner) {

        String userName;
        String passWord;

        System.out.println("输入用户名：");
        userName = scanner.nextLine();
        System.out.println("输入密码：");
        passWord = scanner.nextLine();

        simClient.register(userName, passWord);
    }
}
