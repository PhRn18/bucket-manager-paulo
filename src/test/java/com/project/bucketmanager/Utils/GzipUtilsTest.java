package com.project.bucketmanager.Utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GzipUtilsTest {
    @Test
    void getCompressedBytesUsingGZIP() {
        String content = "mock-content";
        byte[] contentBytes = content.getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("file.txt", contentBytes);

        byte[] compressedBytes = assertDoesNotThrow(() ->
                GzipUtils.getCompressedBytesUsingGZIP(multipartFile.getInputStream()));

        assertThat(compressedBytes).isNotEmpty();
    }
}