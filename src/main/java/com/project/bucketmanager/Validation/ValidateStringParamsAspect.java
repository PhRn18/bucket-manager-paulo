package com.project.bucketmanager.Validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidateStringParamsAspect {
    @Before("@annotation(ValidateStringParams)")
    public void validateStringParam(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof String) {
                String key = (String) arg;
                if (key == null || key.isEmpty()) {
                    throw new IllegalArgumentException("Param must not be null or empty");
                }
            }
        }
    }
}
