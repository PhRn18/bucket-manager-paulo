package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class InvalidBearerTokenException extends RuntimeException{
    public InvalidBearerTokenException(String message) {
        super(message);
    }
    public InvalidBearerTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
