package com.project.bucketmanager.Models;

import java.util.List;

public class CountExtensionOccurrences {
    private String extension;
    private List<String> fileName;
    private int occurrences;

    public CountExtensionOccurrences(String extension, int occurrences,List<String> fileName) {
        this.extension = extension;
        this.occurrences = occurrences;
        this.fileName = fileName;
    }

    public CountExtensionOccurrences() {
    }

    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public static CountExtensionOccurrences buildEmptyResponse(){
        return new CountExtensionOccurrences(null,0,null);
    }
}
