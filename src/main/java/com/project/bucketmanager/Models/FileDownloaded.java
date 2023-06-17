package com.project.bucketmanager.Models;

import org.springframework.core.io.InputStreamResource;

public class FileDownloaded {
    private InputStreamResource inputStreamResource;
    private String contentType;

    public FileDownloaded(InputStreamResource inputStreamResource, String contentType) {
        this.inputStreamResource = inputStreamResource;
        this.contentType = contentType;
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
