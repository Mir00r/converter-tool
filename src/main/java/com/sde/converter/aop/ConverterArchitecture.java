package com.sde.converter.aop;

import org.aspectj.lang.annotation.Pointcut;

public abstract class ConverterArchitecture {

    @Pointcut("within(com.sde.converter.services..*)" + "")
    protected void inServiceLayer() {}

    @Pointcut ("within(com.sde.converter.webservices..*)")
    protected void inWebServicesLayer() {}

    @Pointcut ("within(com.sde.converter.repositories..*)")
    protected void inDAOLayer() {}

    @Pointcut ("within(com.sde.converter.controllers..*)")
    protected void inControllerLayer() {}
}
