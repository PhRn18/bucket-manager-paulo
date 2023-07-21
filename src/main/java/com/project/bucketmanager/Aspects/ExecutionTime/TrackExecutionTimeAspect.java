package com.project.bucketmanager.Aspects.ExecutionTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Aspect
@Component
public class TrackExecutionTimeAspect {

    @Around("@annotation(TrackExecutionTime)")
    public void trackExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

    }

}
