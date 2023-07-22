package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class InvalidMetricNameException extends RuntimeException{
    public InvalidMetricNameException(String message) {
        super(message);
    }

    public InvalidMetricNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
