package com.project.bucketmanager.Validation;

import com.project.bucketmanager.Models.BucketDetails;
import com.project.bucketmanager.Services.BucketService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

import static com.project.bucketmanager.Utils.S3ValidationUtils.bucketExists;
import static com.project.bucketmanager.Utils.S3ValidationUtils.bucketNameMatchRegex;


@Aspect
@Component
public class ValidateStringParamsAspect {
    private final BucketService bucketService;
    public ValidateStringParamsAspect(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Before("@annotation(com.project.bucketmanager.Validation.Annotations.ValidateStringParams)")
    public void validateStringParam(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof String parameterValue) {
                String parameterName = method.getParameters()[i].getName();

                if (parameterValue == null || parameterValue.isEmpty()) {
                    throw new IllegalArgumentException("Param " + parameterName + " must not be null or empty");
                }

                if (parameterName.equals("bucketName")) {
                    List<BucketDetails> buckets = bucketService.listAllBuckets();
                    if (!bucketExists(buckets,parameterValue)) {
                        throw new IllegalArgumentException("Bucket does not exist: "+parameterValue);
                    }
                    if(!bucketNameMatchRegex(parameterValue)){
                        throw new IllegalArgumentException("Invalid bucket name: "+parameterValue);
                    }
                }
            }
        }
    }


}
