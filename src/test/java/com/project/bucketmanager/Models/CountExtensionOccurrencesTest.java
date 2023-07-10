package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@SpringBootTest
class CountExtensionOccurrencesTest {

    @Test
    void testConstructorAndGetters() {
        String extension = "jpg";
        List<String> fileNames = Arrays.asList("file1.jpg", "file2.jpg", "file3.jpg");
        int occurrences = fileNames.size();

        CountExtensionOccurrences countExtensionOccurrences = new CountExtensionOccurrences(extension, occurrences, fileNames);

        assertThat(countExtensionOccurrences.getExtension()).isEqualTo(extension);
        assertThat(countExtensionOccurrences.getOccurrences()).isEqualTo(occurrences);
        assertThat(countExtensionOccurrences.getFileName()).containsExactlyElementsOf(fileNames);
    }

    @Test
    void testSetters() {
        String extension = "jpg";
        List<String> fileNames = Arrays.asList("file1.jpg", "file2.jpg", "file3.jpg");
        int occurrences = fileNames.size();

        CountExtensionOccurrences countExtensionOccurrences = new CountExtensionOccurrences(null, 0, null);

        countExtensionOccurrences.setExtension(extension);
        countExtensionOccurrences.setOccurrences(occurrences);
        countExtensionOccurrences.setFileName(fileNames);

        assertThat(countExtensionOccurrences.getExtension()).isEqualTo(extension);
        assertThat(countExtensionOccurrences.getOccurrences()).isEqualTo(occurrences);
        assertThat(countExtensionOccurrences.getFileName()).containsExactlyElementsOf(fileNames);
    }

    @Test
    void testBuildEmptyResponse() {
        CountExtensionOccurrences countExtensionOccurrences = CountExtensionOccurrences.buildEmptyResponse();

        assertThat(countExtensionOccurrences.getExtension()).isNull();
        assertThat(countExtensionOccurrences.getOccurrences()).isEqualTo(0);
        assertThat(countExtensionOccurrences.getFileName()).isEmpty();
    }

}