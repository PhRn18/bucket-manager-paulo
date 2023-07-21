package com.project.bucketmanager.Aspects.Logs;

import com.project.bucketmanager.Models.Log;
import com.project.bucketmanager.Repository.LogsRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.project.bucketmanager.Aspects.AspectUtils.findParameterByName;

@Aspect
@Component
public class RegisterLogsAspect {
    private final LogsRepository logsRepository;
    public RegisterLogsAspect(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }
    @Pointcut("@within(com.project.bucketmanager.Aspects.Logs.RegisterLogs) && execution(* *(..))")
    public void inRegisterLogsClass() {
    }

    @AfterReturning(pointcut = "inRegisterLogsClass()")
    public void registerSuccessLog(JoinPoint joinPoint){
        Log log = buildDefaultLog(joinPoint);
        log.setException(null);
        logsRepository.create(log);
    }

    @AfterThrowing(pointcut = "inRegisterLogsClass()", throwing = "ex")
    public void registerFailLog(JoinPoint joinPoint, Exception ex){
        Log log = buildDefaultLog(joinPoint);
        log.setException(ex.getClass().getSimpleName());
        logsRepository.create(log);
    }

    private Log buildDefaultLog(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String time = LocalDateTime.now().toString();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Log log = new Log();


        Optional<String> bucketName = findParameterByName(methodSignature,args,"bucketName",String.class);

        String user = (authentication != null) ? authentication.getName() : "UserNotAuthenticated";

        bucketName.ifPresent(log::setBucket);
        log.setOperation(methodName);
        log.setTime(time);
        log.setUser(user);
        return log;
    }

}
