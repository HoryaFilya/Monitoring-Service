package com.shaikhraziev.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Loggable {

    @Pointcut("@within(com.shaikhraziev.aop.annotations.Loggable) && " +
              "execution(public java.util.Optional com.shaikhraziev.repository.IndicationRepository.getActualIndications(Long))")
    public void annotatedByLoggable() {
    }

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Calling method " + proceedingJoinPoint.getSignature());

        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();

        System.out.println("Execution of method " + proceedingJoinPoint.getSignature() +
                           " finished. Execution time is " + (endTime - startTime) + " ms");
        return result;
    }
}