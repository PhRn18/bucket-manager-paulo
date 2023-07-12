package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class FoldersSizeTest {
    @Test
    void testAddFolder() {
        FoldersSize foldersSize = new FoldersSize();
        foldersSize.addFolder("Folder1", "10.5");
        foldersSize.addFolder("Folder2", "5.5");

        List<Map<String, String>> expectedFolders = new ArrayList<>();
        Map<String, String> folder1 = new HashMap<>();
        folder1.put("Folder1", "10.5");
        Map<String, String> folder2 = new HashMap<>();
        folder2.put("Folder2", "5.5");
        expectedFolders.add(folder1);
        expectedFolders.add(folder2);

        assertThat(foldersSize.getFolders()).isEqualTo(expectedFolders);
    }

    @Test
    void testBuildEmptyResponse() {
        FoldersSize foldersSize = FoldersSize.buildEmptyResponse();

        assertThat(foldersSize.getTotalSize()).isEqualTo(0.00);
        assertThat(foldersSize.getNumberOfFolders()).isEqualTo(0);
        assertThat(foldersSize.getFolders().size()).isZero();
    }
}