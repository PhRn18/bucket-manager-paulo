package com.project.bucketmanager.Models;

import org.springframework.core.io.InputStreamResource;

public class FileDownloaded {
    private InputStreamResource inputStreamResource;
    private String contentType;
    private String fileName;

    public FileDownloaded(InputStreamResource inputStreamResource, String contentType, String fileName) {
        this.inputStreamResource = inputStreamResource;
        this.contentType = contentType;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStreamResource getInputStreamResource() {
        return inputStreamResource;
    }

    public void setInputStreamResource(InputStreamResource inputStreamResource) {
        this.inputStreamResource = inputStreamResource;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
