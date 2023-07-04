package com.project.bucketmanager.Models;

public class CompressedFileUpdate {
    private String fileName;
    private long originalFileSize;
    private long compressedFileSize;
    private String compressRate;

    public CompressedFileUpdate() {
    }

    public CompressedFileUpdate(long originalFileSize, long compressedFileSize) {
        this.originalFileSize = originalFileSize;
        this.compressedFileSize = compressedFileSize;
        double rate = (double) originalFileSize / compressedFileSize;
        this.compressRate = String.format("%.2f", rate);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getOriginalFileSize() {
        return originalFileSize;
    }

    public void setOriginalFileSize(long originalFileSize) {
        this.originalFileSize = originalFileSize;
    }

    public long getCompressedFileSize() {
        return compressedFileSize;
    }

    public void setCompressedFileSize(long compressedFileSize) {
        this.compressedFileSize = compressedFileSize;
    }

    public String getCompressRate() {
        return compressRate;
    }

    public void setCompressRate(String compressRate) {
        this.compressRate = compressRate;
    }
}
