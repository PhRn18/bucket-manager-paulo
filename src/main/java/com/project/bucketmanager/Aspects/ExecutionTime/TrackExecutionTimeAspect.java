package com.project.bucketmanager.Aspects.ExecutionTime;

import com.project.bucketmanager.ExceptionHandler.Exceptions.TrackExecutionTimeException;
import com.project.bucketmanager.Models.ExecutionTime;
import com.project.bucketmanager.Repository.ExecutionTimeRepository;
import com.project.bucketmanager.Repository.Impl.ExecutionTimeRepositoryImpl;
import com.project.bucketmanager.Repository.LogsRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Aspect
@Component
public class TrackExecutionTimeAspect {
    private final ExecutionTimeRepository executionTimeRepository;
    public TrackExecutionTimeAspect(ExecutionTimeRepository executionTimeRepository) {
        this.executionTimeRepository = executionTimeRepository;
    }
    @Around("@annotation(TrackExecutionTime)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        long beforeMethodExecution = Instant.now().toEpochMilli();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new TrackExecutionTimeException("Unable to track and register the execution time");
        }
        long afterMethodExecution = Instant.now().toEpochMilli();
        long time = afterMethodExecution - beforeMethodExecution;
        ExecutionTime executionTime = new ExecutionTime(methodName,time,0L,0L);
        executionTimeRepository.create(executionTime);
        return result;
    }

}
