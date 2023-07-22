package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class ParameterRecoveryException extends RuntimeException{
    public ParameterRecoveryException(String message) {
        super(message);
    }

    public ParameterRecoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
