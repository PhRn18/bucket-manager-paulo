package com.project.bucketmanager.Aspects.Validation.Utils;

import org.aspectj.lang.reflect.MethodSignature;

import java.util.Optional;

public class ValidationUtils {
    public static  <T> Optional<T> findParameterByName(
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
