package com.project.bucketmanager.Models;


import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ListAllFileExtensionsTest {
    @Test
    void testBuildEmptyResponse() {
        ListAllFileExtensions fileExtensions = ListAllFileExtensions.buildEmptyResponse();

        assertThat(fileExtensions.getFileExtensions()).isNull();
        assertThat(fileExtensions.getNumberOfExtensions()).isEqualTo(0);
    }

    @Test
    void testSetAndGetFileExtensions() {
        ListAllFileExtensions fileExtensions = new ListAllFileExtensions();
        Set<String> extensions = new HashSet<>();
        extensions.add(".txt");
        extensions.add(".doc");
        extensions.add(".pdf");

        fileExtensions.setFileExtensions(extensions);

        assertThat(fileExtensions.getFileExtensions()).isEqualTo(extensions);
    }

    @Test
    void testSetAndGetNumberOfExtensions() {
        ListAllFileExtensions fileExtensions = new ListAllFileExtensions();
        int numberOfExtensions = 5;

        fileExtensions.setNumberOfExtensions(numberOfExtensions);

        assertThat(fileExtensions.getNumberOfExtensions()).isEqualTo(numberOfExtensions);
    }
}