package com.project.bucketmanager.ExceptionHandler.Exceptions;

public class TrackExecutionTimeException extends RuntimeException{
    public TrackExecutionTimeException(String message) {
        super(message);
    }

    public TrackExecutionTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
