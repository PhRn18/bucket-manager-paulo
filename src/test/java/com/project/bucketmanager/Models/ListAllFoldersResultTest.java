package com.project.bucketmanager.Models;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ListAllFoldersResultTest {
    @Test
    void testBuildEmptyResponse() {
        ListAllFoldersResult result = ListAllFoldersResult.buildEmptyResponse();

        assertThat(result.getFolders()).isNull();
        assertThat(result.getNumberOfFolders()).isEqualTo(0);
    }

    @Test
    void testSetAndGetFolders() {
        ListAllFoldersResult result = new ListAllFoldersResult();
        Set<String> folders = new HashSet<>();
        folders.add("Folder1");
        folders.add("Folder2");
        folders.add("Folder3");

        result.setFolders(folders);

        assertThat(result.getFolders()).isEqualTo(folders);
    }

    @Test
    void testSetAndGetNumberOfFolders() {
        ListAllFoldersResult result = new ListAllFoldersResult();
        int numberOfFolders = 5;

        result.setNumberOfFolders(numberOfFolders);

        assertThat(result.getNumberOfFolders()).isEqualTo(numberOfFolders);
    }
}