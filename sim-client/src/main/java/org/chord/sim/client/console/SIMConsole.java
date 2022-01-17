package org.chord.sim.client.console;

import org.chord.sim.client.console.command.ConsoleCommandManager;
import org.chord.sim.client.util.SessionUtil;

import java.util.Scanner;

/**
 * @author chord
 * date 2022/1/17 22:01
 * function:
 */

public class SIMConsole implements Runnable{
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager();

        while (!Thread.interrupted()) {
            consoleCommandManager.exec(scanner);
        }
    }
}
