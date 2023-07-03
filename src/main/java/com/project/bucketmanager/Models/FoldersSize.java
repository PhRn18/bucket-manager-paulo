package com.project.bucketmanager.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoldersSize {
    private List<Map<String,String>> folders = new ArrayList<>();

    private int numberOfFolders;

    private double totalSize;

    public FoldersSize() {
    }

    public FoldersSize(List<Map<String, String>> folders, int numberOfFolders, double totalSize) {
        this.folders = folders;
        this.numberOfFolders = numberOfFolders;
        this.totalSize = totalSize;
    }

    public List<Map<String, String>> getFolders() {
        return folders;
    }

    public void setFolders(List<Map<String, String>> folders) {
        this.folders = folders;
    }

    public int getNumberOfFolders() {
        return numberOfFolders;
    }

    public void setNumberOfFolders(int numberOfFolders) {
        this.numberOfFolders = numberOfFolders;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(double totalSize) {
        this.totalSize = totalSize;
    }

    public void addFolder(String folderName, String folderSize) {
        boolean folderExists = false;

        for (Map<String, String> folderEntry : folders) {
            if (folderEntry.containsKey(folderName)) {
                folderExists = true;
                double currentSize = Double.parseDouble(folderEntry.get(folderName));
                double newSize = currentSize + Double.parseDouble(folderSize);
                folderEntry.put(folderName, String.valueOf(newSize));
                break;
            }
        }

        if (!folderExists) {
            Map<String, String> newFolderEntry = new HashMap<>();
            newFolderEntry.put(folderName, folderSize);
            folders.add(newFolderEntry);
        }
    }


    public static FoldersSize buildEmptyResponse(){
        FoldersSize foldersSize = new FoldersSize();
        foldersSize.setTotalSize(0.00);
        foldersSize.setNumberOfFolders(0);
        return foldersSize;
    }
}
