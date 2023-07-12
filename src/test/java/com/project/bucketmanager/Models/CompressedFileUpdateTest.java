package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CompressedFileUpdateTest {
    @Test
    public void testConstructorAndGetters() {
        String fileName = "file.txt";
        long originalFileSize = 1000L;
        long compressedFileSize = 500L;

        CompressedFileUpdate compressedFileUpdate = new CompressedFileUpdate(fileName, originalFileSize, compressedFileSize);

        assertThat(compressedFileUpdate.getFileName()).isEqualTo(fileName);
        assertThat(compressedFileUpdate.getOriginalFileSize()).isEqualTo(originalFileSize);
        assertThat(compressedFileUpdate.getCompressedFileSize()).isEqualTo(compressedFileSize);

        double expectedRate = (double) originalFileSize / compressedFileSize;
        String expectedRateString = String.format("%.2f", expectedRate);
        assertThat(compressedFileUpdate.getCompressRate()).isEqualTo(expectedRateString);
    }

    @Test
    public void testSetters() {
        CompressedFileUpdate compressedFileUpdate = new CompressedFileUpdate();

        String fileName = "file.txt";
        long originalFileSize = 1000L;
        long compressedFileSize = 500L;
        String compressRate = "2.00";

        compressedFileUpdate.setFileName(fileName);
        compressedFileUpdate.setOriginalFileSize(originalFileSize);
        compressedFileUpdate.setCompressedFileSize(compressedFileSize);
        compressedFileUpdate.setCompressRate(compressRate);

        assertThat(compressedFileUpdate.getFileName()).isEqualTo(fileName);
        assertThat(compressedFileUpdate.getOriginalFileSize()).isEqualTo(originalFileSize);
        assertThat(compressedFileUpdate.getCompressedFileSize()).isEqualTo(compressedFileSize);
        assertThat(compressedFileUpdate.getCompressRate()).isEqualTo(compressRate);
    }
}