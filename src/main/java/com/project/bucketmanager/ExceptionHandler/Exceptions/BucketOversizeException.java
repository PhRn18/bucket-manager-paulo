package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class BucketOversizeException extends RuntimeException{
    public BucketOversizeException(String message) {
        super(message);
    }

    public BucketOversizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
