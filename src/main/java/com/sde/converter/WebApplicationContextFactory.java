package com.sde.converter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;

import javax.servlet.ServletConfig;

@Component("webApplicationContextFactory")
public class WebApplicationContextFactory implements ApplicationContextAware, ServletConfigAware {
    private static ApplicationContext APPLICATION_CONTEXT;
    private static ServletConfig SERVLET_CONFIG;

    public WebApplicationContextFactory() {
    }

    public static MessageSource getMessageSource() {
        return (MessageSource)APPLICATION_CONTEXT.getBean("messageSource");
    }

    public static ServletConfig getServletConfig() {
        return SERVLET_CONFIG;
    }

    public void setApplicationContext(ApplicationContext var1) throws BeansException {
        APPLICATION_CONTEXT = var1;
    }

    public void setServletConfig(ServletConfig var1) {
        SERVLET_CONFIG = var1;
    }
}
