package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class ListAllBucketsException extends RuntimeException{
    public ListAllBucketsException(String message) {
        super(message);
    }

    public ListAllBucketsException(String message, Throwable cause) {
        super(message, cause);
    }
}
