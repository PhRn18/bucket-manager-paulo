package com.project.bucketmanager.Models;

import java.util.List;
import java.util.Set;

public class ListAllFoldersResult {
    private Set<String> folders;

    private int numberOfFolders;

    public ListAllFoldersResult() {
    }

    public ListAllFoldersResult(Set<String> folders,int numberOfFolders) {
        this.folders = folders;
        this.numberOfFolders=numberOfFolders;
    }

    public int getNumberOfFolders() {
        return numberOfFolders;
    }

    public void setNumberOfFolders(int numberOfFolders) {
        this.numberOfFolders = numberOfFolders;
    }

    public Set<String> getFolders() {
        return folders;
    }

    public void setFolders(Set<String> folders) {
        this.folders = folders;
    }

    public static ListAllFoldersResult buildEmptyResponse(){
        return new ListAllFoldersResult(null,0);
    }
}
