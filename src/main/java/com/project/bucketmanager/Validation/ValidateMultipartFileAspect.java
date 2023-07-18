package com.project.bucketmanager.Validation;

import com.project.bucketmanager.ExceptionHandler.Exceptions.EmptyFileException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class ValidateMultipartFileAspect {
    @Before("@annotation(com.project.bucketmanager.Validation.Annotations.ValidateMultipartFile)")
    public void validateFile(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        for(Object arg:args){
            if(arg instanceof MultipartFile){
                boolean empty = ((MultipartFile) arg).isEmpty();
                if(empty){
                    throw new EmptyFileException("The file " + ((MultipartFile) arg).getOriginalFilename() + " is empty!");
                }
            }
        }
    }
}
