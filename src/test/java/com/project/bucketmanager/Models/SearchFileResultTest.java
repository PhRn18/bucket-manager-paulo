package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SearchFileResultTest {
    @Test
    void testSetAndGetKey() {
        SearchFileResult result = new SearchFileResult();
        List<String> key = List.of("file1.txt", "file2.txt", "file3.txt");

        result.setKey(key);

        assertThat(result.getKey()).isEqualTo(key);
    }

    @Test
    void testSetAndGetIsExactFileNamePresent() {
        SearchFileResult result = new SearchFileResult();
        boolean isExactFileNamePresent = true;

        result.setExactFileNamePresent(isExactFileNamePresent);

        assertThat(result.isExactFileNamePresent()).isEqualTo(isExactFileNamePresent);
    }

    @Test
    void testSetAndGetIsAnotherFilesWithTheCharSequence() {
        SearchFileResult result = new SearchFileResult();
        boolean isAnotherFilesWithTheCharSequence = false;

        result.setAnotherFilesWithTheCharSequence(isAnotherFilesWithTheCharSequence);

        assertThat(result.isAnotherFilesWithTheCharSequence()).isEqualTo(isAnotherFilesWithTheCharSequence);
    }
}