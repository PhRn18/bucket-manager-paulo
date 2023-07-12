package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class FileDownloadedTest {

    @Test
    void testConstructorAndGetters(){
        byte[] content = "Conteúdo do arquivo".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        InputStreamResource resource = new InputStreamResource(inputStream);
        String contentType = "application/json";
        String filename = "mockfile";

        FileDownloaded fileDownloaded = new FileDownloaded(resource,contentType,filename);

        assertThat(fileDownloaded.getFileName()).isEqualTo(filename);
        assertThat(fileDownloaded.getContentType()).isEqualTo(contentType);
        assertThat(fileDownloaded.getInputStreamResource()).isEqualTo(resource);
    }

    @Test
    void testSetters(){
        byte[] content = "Conteúdo do arquivo".getBytes();
        byte[] content2 = "".getBytes();

        InputStream inputStream = new ByteArrayInputStream(content);
        InputStream inputStream2 = new ByteArrayInputStream(content2);

        InputStreamResource resource = new InputStreamResource(inputStream);
        InputStreamResource resource2 = new InputStreamResource(inputStream2);

        String contentType = "application/json";
        String filename = "mockfile";

        FileDownloaded fileDownloaded = new FileDownloaded(resource2,"","");


        fileDownloaded.setFileName(filename);
        fileDownloaded.setContentType(contentType);
        fileDownloaded.setInputStreamResource(resource);


        assertThat(fileDownloaded.getFileName()).isEqualTo(filename);
        assertThat(fileDownloaded.getContentType()).isEqualTo(contentType);
        assertThat(fileDownloaded.getInputStreamResource()).isEqualTo(resource);
    }


}