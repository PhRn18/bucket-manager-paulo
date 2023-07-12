package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileUploadedTest {
    @Test
    void testConstructorAndGetters(){
        String message = "fileuploaded";
        long fileSize = 30L;
        String fileName = "picture.png";
        FileUploaded fileUploaded = new FileUploaded(message,fileSize,fileName);

        assertThat(fileUploaded.getMessage()).isEqualTo(message);
        assertThat(fileUploaded.getFileSize()).isEqualTo(fileSize);
        assertThat(fileUploaded.getFileName()).isEqualTo(fileName);
    }
    @Test
    void testSetters(){
        String message = "fileuploaded";
        long fileSize = 30L;
        String fileName = "picture.png";
        FileUploaded fileUploaded = new FileUploaded("",0L,"");

        fileUploaded.setFileSize(fileSize);
        fileUploaded.setFileName(fileName);
        fileUploaded.setMessage(message);


        assertThat(fileUploaded.getMessage()).isEqualTo(message);
        assertThat(fileUploaded.getFileSize()).isEqualTo(fileSize);
        assertThat(fileUploaded.getFileName()).isEqualTo(fileName);
    }
}