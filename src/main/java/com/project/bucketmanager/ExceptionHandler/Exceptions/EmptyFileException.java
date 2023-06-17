package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class EmptyFileException extends RuntimeException{
    public EmptyFileException(String message) {
        super(message);
    }

    public EmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
