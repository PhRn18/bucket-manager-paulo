package com.project.bucketmanager.Validation;

import com.project.bucketmanager.ExceptionHandler.Exceptions.BucketOversizeException;
import com.project.bucketmanager.ExceptionHandler.Exceptions.FileUploadException;
import com.project.bucketmanager.Models.Content;
import com.project.bucketmanager.Services.BucketService;
import com.project.bucketmanager.Validation.Annotations.OverrideMaxBucketSize;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
public class CheckBucketSizeAspect {
    private final BucketService bucketService;
    private Long maxSize;
    public CheckBucketSizeAspect(BucketService bucketService, @Value("${aws.bucket.maxSize}")String maxSize) {
        this.bucketService = bucketService;
        this.maxSize = Long.parseLong(maxSize);
    }
    @Before("@annotation(com.project.bucketmanager.Validation.Annotations.CheckBucketSize)")
    public void beforeMethodExecution(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        Optional<String> optionalBucketName = findParameterByName(
                methodSignature,
                args,
                "bucketName",
                String.class);

        Optional<MultipartFile> optionalMultipartFile = findParameterByName(
                methodSignature,
                args,
                "file",
                MultipartFile.class);

        getOverriddenMaxBucketSize(method).ifPresent(aLong -> maxSize = aLong);

        String bucketName = optionalBucketName.orElseThrow(
                () -> new FileUploadException("Failed to retrieve the parameter 'bucketName' to validate the total size of the bucket")
        );

        MultipartFile multipartFile = optionalMultipartFile.orElseThrow(
                () -> new FileUploadException("Failed to retrieve the parameter 'file' to validate the total size of the bucket")
        );

        long bucketSize = bucketService.listAllBucketContent(bucketName)
                .getObjectList()
                .stream()
                .mapToLong(Content::getSize)
                .sum();

        long fileSize = multipartFile.getSize();

        long postModificationBucketSize = bucketSize + fileSize;

        if(postModificationBucketSize > maxSize){
            long oversize = postModificationBucketSize - maxSize;
            throw new BucketOversizeException("Max bucket size exceeded in "+oversize+"Bytes");
        }
    }

    protected Optional<Long> getOverriddenMaxBucketSize(Method method){
        OverrideMaxBucketSize overrideMaxBucketSize =  method.getAnnotation(OverrideMaxBucketSize.class);
        if(overrideMaxBucketSize != null){
            long overriddenMaxBucketSize = overrideMaxBucketSize.size();
            if(overriddenMaxBucketSize < 0L){
                throw new IllegalArgumentException("Invalid max bucket size: "+ overriddenMaxBucketSize);
            }
            return Optional.of(overriddenMaxBucketSize);
        }
        return Optional.empty();
    }

    protected  <T> Optional<T> findParameterByName(
            MethodSignature methodSignature,
            Object[] args,
            String parameterName,
            Class<T> parameterType
    ) {
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterName.equals(parameterNames[i])) {
                return Optional.of(parameterType.cast(args[i]));
            }
        }
        return Optional.empty();
    }
}
