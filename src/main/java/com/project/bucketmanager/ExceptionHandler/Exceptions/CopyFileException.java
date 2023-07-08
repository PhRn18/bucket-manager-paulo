package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class CopyFileException extends RuntimeException{
    public CopyFileException(String message) {
        super(message);
    }

    public CopyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
