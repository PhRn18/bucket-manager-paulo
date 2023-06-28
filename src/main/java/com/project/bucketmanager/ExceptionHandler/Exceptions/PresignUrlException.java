package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class PresignUrlException extends RuntimeException{
    public PresignUrlException(String message) {
        super(message);
    }

    public PresignUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
