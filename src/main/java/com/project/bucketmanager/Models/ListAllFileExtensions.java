package com.project.bucketmanager.Models;

import java.util.List;
import java.util.Set;

public class ListAllFileExtensions {
    private Set<String> fileExtensions;
    private int numberOfExtensions;

    public ListAllFileExtensions(Set<String> fileExtensions, int numberOfExtensions) {
        this.fileExtensions = fileExtensions;
        this.numberOfExtensions = numberOfExtensions;
    }

    public ListAllFileExtensions() {
    }

    public Set<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(Set<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public int getNumberOfExtensions() {
        return numberOfExtensions;
    }

    public void setNumberOfExtensions(int numberOfExtensions) {
        this.numberOfExtensions = numberOfExtensions;
    }

    public static ListAllFileExtensions buildEmptyResponse(){
        return new ListAllFileExtensions(null,0);
    }
}
