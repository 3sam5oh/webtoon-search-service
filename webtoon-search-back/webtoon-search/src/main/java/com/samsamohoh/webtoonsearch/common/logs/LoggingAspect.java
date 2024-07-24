package com.samsamohoh.webtoonsearch.common.logs;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingAspect {

    @Pointcut("execution(* com.samsamohoh.webtoonsearch.*(..))")
    public void activeLogPointcut() {}
}
