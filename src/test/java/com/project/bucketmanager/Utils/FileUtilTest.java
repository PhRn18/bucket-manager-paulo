package com.project.bucketmanager.Utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FileUtilTest {

    @Test
    void getFolderFromKey() {
        String key = "c/folder/result.txt";
        String folder = FileUtil.getFolderFromKey(key);
        assertThat(folder).isNotNull();
        assertThat(folder).isEqualTo("c/folder");
    }

    @Test
    void getExtensionFromKey() {
        String key = "c/folder/result.txt";
        String extension = FileUtil.getExtensionFromKey(key);
        assertThat(extension).isNotNull();
        assertThat(extension).isEqualTo("txt");
    }

    @Test
    void getFileNameWithoutExtension() {
        String key = "result.txt";
        String fileName = FileUtil.getFileNameWithoutExtension(key);
        assertThat(fileName).isNotNull();
        assertThat(fileName).isEqualTo("result");
    }

    @Test
    void getOriginalFileExtension() {
        String key = "result-txt.gz";
        String originalFileExtension = FileUtil.getOriginalFileExtension(key);
        assertThat(originalFileExtension).isNotNull();
        assertThat(originalFileExtension).isEqualTo("txt");
    }
}