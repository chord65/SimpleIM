package org.chord.sim.client;

import org.chord.sim.client.console.SIMConsole;
import org.chord.sim.client.util.SessionUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chord
 * date 2022/1/16 18:47
 * function:
 */

@SpringBootApplication
public class SIMClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SIMClientApplication.class, args);

        new Thread(new SIMConsole()).start();

    }
}
