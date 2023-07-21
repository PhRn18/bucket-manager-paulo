package com.project.bucketmanager.Models;

public class Log {
    private String bucket;
    private String user;
    private String operation;
    private String time;
    private String exception;

    public Log(String bucket, String user, String operation, String time, String exception) {
        this.bucket = bucket;
        this.user = user;
        this.operation = operation;
        this.time = time;
        this.exception = exception;
    }

    public Log() {
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
