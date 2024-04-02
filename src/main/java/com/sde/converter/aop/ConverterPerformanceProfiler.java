package com.sde.converter.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Aspect
public class ConverterPerformanceProfiler extends ConverterArchitecture {

    private static Logger log = LoggerFactory.getLogger(ConverterPerformanceProfiler.class);

    @Around("inServiceLayer() || inWebServicesLayer() || inDAOLayer() || inControllerLayer()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return pjp.proceed();
        } finally {
            long elapsedTime = System.currentTimeMillis() - start;
            log.debug("Method '" + pjp.getSignature().toShortString() + "' execution time = [" + elapsedTime + "] ms.");
        }
    }

}
