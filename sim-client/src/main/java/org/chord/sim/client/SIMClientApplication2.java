package org.chord.sim.client;

import org.chord.sim.client.console.SIMConsole;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chord
 * date 2022/1/16 18:47
 * function:
 */

@SpringBootApplication
public class SIMClientApplication2 {

    public static void main(String[] args) {
        SpringApplication.run(SIMClientApplication2.class, args);

        new Thread(new SIMConsole()).start();

    }
}
