package org.chord.sim.router;

import org.chord.sim.router.util.ServerListListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chord
 * date 2022/1/12 10:46
 * function:
 */

@SpringBootApplication
public class RouterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouterApplication.class, args);

        new Thread(new ServerListListener()).start();
    }

}
