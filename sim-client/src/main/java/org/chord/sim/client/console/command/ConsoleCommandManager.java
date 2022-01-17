package org.chord.sim.client.console.command;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author chord
 * date 2022/1/17 22:27
 * function:
 */
public class ConsoleCommandManager implements ConsoleCommand{
    private Map<String, ConsoleCommand> consoleCommandMap;

    public ConsoleCommandManager() {
        consoleCommandMap = new HashMap<>();
        consoleCommandMap.put("register", new RegisterConsoleCommand());
    }

    @Override
    public void exec(Scanner scanner) {
        //  获取第一个指令
        System.out.println("输入指令：");
        String command = scanner.next();

        ConsoleCommand consoleCommand = consoleCommandMap.get(command);

        if (consoleCommand != null) {
            consoleCommand.exec(scanner);
        } else {
            System.err.println("无法识别[" + command + "]指令，请重新输入!");
        }
    }
}
