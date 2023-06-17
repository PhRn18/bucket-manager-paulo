package com.project.bucketmanager.Models;

public class FileUploaded {
    private String message;
    private Long fileSize;
    private String fileName;

    public FileUploaded(String message, Long fileSize, String fileName) {
        this.message = message;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
