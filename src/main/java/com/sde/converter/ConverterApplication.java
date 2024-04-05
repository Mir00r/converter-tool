package com.sde.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.ClassUtils;

@SpringBootApplication
//@ComponentScan(basePackages = {
//        "com.sde.converter.aop",
//        "com.sde.converter.**.services",
////        "com.sde.converter.**.listener",
//        "com.sde.converter.**.configs"
//})
public class ConverterApplication {

    public static void main(String[] args) {
        // for log4j Thread usage
        // Data from current threads will be passed to child threads.
//        System.setProperty("isThreadContextMapInheritable", "true");
//
//        // default for everyone
//        System.setProperty("org.jboss.logging.provider", "slf4j");
//
//        // to cater Tomcat logging
//        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
//
//        // to make all loggers asynchronous
//        boolean lmaxDisruptorPresent = ClassUtils.isPresent("com.lmax.disruptor.dsl.Disruptor", ConverterApplication.class.getClassLoader());
//        if (lmaxDisruptorPresent) {
//            System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
//            System.setProperty("log4j2.asyncLoggerRingBufferSize", (1024 * 1024) + ""); // default is 256 * 1024
//        }

        SpringApplication.run(ConverterApplication.class, args);
    }

}
