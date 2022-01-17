package org.chord.sim.router.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author chord
 * date 2022/1/17 19:57
 * function:
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
