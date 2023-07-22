package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class InvalidStatisticTypeException extends RuntimeException{
    public InvalidStatisticTypeException(String message) {
        super(message);
    }

    public InvalidStatisticTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
