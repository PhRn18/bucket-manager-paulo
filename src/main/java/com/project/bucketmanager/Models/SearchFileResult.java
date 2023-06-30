package com.project.bucketmanager.Models;

import java.util.List;

public class SearchFileResult {
    private List<String> key;
    private boolean isExactFileNamePresent;
    private boolean isAnotherFilesWithTheCharSequence;

    public SearchFileResult() {
    }

    public SearchFileResult(List<String> key, boolean isExactFileNamePresent,boolean isAnotherFilesWithTheCharSequence) {
        this.key = key;
        this.isExactFileNamePresent = isExactFileNamePresent;
        this.isAnotherFilesWithTheCharSequence=isAnotherFilesWithTheCharSequence;
    }

    public List<String> getKey() {
        return key;
    }

    public void setKey(List<String> key) {
        this.key = key;
    }

    public boolean isExactFileNamePresent() {
        return isExactFileNamePresent;
    }

    public void setExactFileNamePresent(boolean exactFileNamePresent) {
        isExactFileNamePresent = exactFileNamePresent;
    }

    public boolean isAnotherFilesWithTheCharSequence() {
        return isAnotherFilesWithTheCharSequence;
    }

    public void setAnotherFilesWithTheCharSequence(boolean anotherFilesWithTheCharSequence) {
        isAnotherFilesWithTheCharSequence = anotherFilesWithTheCharSequence;
    }
}
