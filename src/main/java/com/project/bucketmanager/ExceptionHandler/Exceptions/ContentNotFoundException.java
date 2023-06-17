package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class ContentNotFoundException extends RuntimeException{
    public ContentNotFoundException(String message) {
        super(message);
    }

    public ContentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
