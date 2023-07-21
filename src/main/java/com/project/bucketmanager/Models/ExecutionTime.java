package com.project.bucketmanager.Models;

public class ExecutionTime {
    private String methodName;
    private long executionTime;
    private long lastExecutionTime;
    private long ratio;

    public ExecutionTime(String methodName, long executionTime, long lastExecutionTime, long ratio) {
        this.methodName = methodName;
        this.executionTime = executionTime;
        this.lastExecutionTime = lastExecutionTime;
        this.ratio = ratio;
    }

    public ExecutionTime() {
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public long getRatio() {
        return ratio;
    }

    public void setRatio(long ratio) {
        this.ratio = ratio;
    }
}
