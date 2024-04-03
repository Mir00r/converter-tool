package com.sde.converter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("applicationContextFactory")
public class ApplicationContextFactory implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public ApplicationContextFactory() {
    }

    public void setApplicationContext(ApplicationContext var1) {
        applicationContext = var1;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
