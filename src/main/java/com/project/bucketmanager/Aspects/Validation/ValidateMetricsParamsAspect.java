package com.project.bucketmanager.Aspects.Validation;

import com.project.bucketmanager.ExceptionHandler.Exceptions.InvalidMetricNameException;
import com.project.bucketmanager.ExceptionHandler.Exceptions.InvalidStatisticTypeException;
import com.project.bucketmanager.ExceptionHandler.Exceptions.ParameterRecoveryException;
import com.project.bucketmanager.Models.AvailableMetrics;
import com.project.bucketmanager.Aspects.Validation.Utils.ValidationUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

import java.util.Optional;

@Aspect
@Component
public class ValidateMetricsParamsAspect {
    private final AvailableMetrics availableMetrics;
    public ValidateMetricsParamsAspect(AvailableMetrics availableMetrics) {
        this.availableMetrics = availableMetrics;
    }
    @Before("@annotation(com.project.bucketmanager.Aspects.Validation.Annotations.ValidateMetricsParams)")
    public void validateParams(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        String metricName = getParameter(methodSignature,args,"metricName");
        String typeOfStatistics = getParameter(methodSignature,args,"typeOfStatistics");

        validateMetricAndStatistics(metricName,typeOfStatistics);
    }

    private static String getParameter(MethodSignature methodSignature, Object[] args, String parameterName){
        Optional<String> optionalParameter = ValidationUtils.findParameterByName(methodSignature, args, parameterName, String.class);
        return optionalParameter.orElseThrow(() -> new ParameterRecoveryException("Failed to retrieve the parameter '" + parameterName + "'"));
    }
    private void validateMetricAndStatistics(String metricName, String typeOfStatistics) {
        if (!availableMetrics.metricIsAvailable(metricName)) {
            throw new InvalidMetricNameException("Metric [" + metricName + "] is not enabled or doesn't exist");
        }

        if (Statistic.fromValue(typeOfStatistics) == Statistic.UNKNOWN_TO_SDK_VERSION) {
            throw new InvalidStatisticTypeException("Statistic type [" + typeOfStatistics + "] is not valid");
        }
    }
}
