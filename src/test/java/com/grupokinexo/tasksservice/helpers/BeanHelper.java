package com.grupokinexo.tasksservice.helpers;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanHelper {
    private static final String springConfigPath = "base-config.xml";
    private static final ApplicationContext context = new ClassPathXmlApplicationContext(springConfigPath);

    public static <T> T getBean(String name, Class<T> type) {
        return context.getBean(name, type);
    }
}