package com.project.bucketmanager.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.ExceptionHandler.Exceptions.EmptyFileException;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BucketControllerTest {
    private MockMvc mockMvc;
    @Mock
    private BucketService bucketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BucketController bucketController = new BucketController(bucketService);
        mockMvc = MockMvcBuilders.standaloneSetup(bucketController).build();
    }
    @Test
    void listAllBucketContent() throws Exception {
        String bucketName = "bucket-name";
        BucketContent bucketContent = new BucketContent(List.of(
                new Content("fdsdfsfds","312321213"),
                new Content("grgr","324132321"),
                new Content("1231dsas","fadfda")
        ));
        when(bucketService.listAllBucketContent(bucketName)).thenReturn(bucketContent);

        mockMvc.perform(get("/bucket/{bucketName}",bucketName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void listBucketContentDetails() throws Exception {
        String bucketName = "bucket-1";
        String key = "key-1";
        ContentDetails expectedDetails = new ContentDetails();

        when(bucketService.getBucketContentDetailsByKey(bucketName, key)).thenReturn(expectedDetails);

        mockMvc.perform(get("/bucket/details/{bucketName}/{key}", bucketName, key))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void uploadFileToBucket_WhenFileIsEmpty() throws Exception {
        String bucketName = "bucket-1";
        /*
        doThrow(EmptyFileException.class).when(bucketService).updateFileToBucket(any(MultipartFile.class), anyString());
        mockMvc.perform(
                        multipart("/bucket/{bucketName}", bucketName)
                                .file("file", new byte[0])  // Define um array de bytes vazio como conteúdo do arquivo
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Empty file!"))
                .andReturn();

         */
    }

    @Test
    void uploadFileToBucket_WhenFileIsValid() throws Exception {
        String bucketName = "bucket-1";
        String fileName = "file.txt";
        long fileSize = 100L;
        FileUploaded expectedResponse = new FileUploaded("File uploaded!", fileSize, fileName);

        mockMvc.perform(
                        multipart("/bucket/{bucketName}", bucketName)
                                .file("file", "content".getBytes())
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedResponse.getMessage()))
                .andReturn();

        verify(bucketService).updateFileToBucket(any(MultipartFile.class), eq(bucketName));
    }

    @Test
    void downloadFileFromBucketWithContentDispositionInline() throws Exception {
        String bucketName = "bucket-1";
        String key = "file.txt";
        String contentDisposition = "inline";

        FileDownloaded fileDownloaded = new FileDownloaded(
                new InputStreamResource(new ByteArrayInputStream("content".getBytes())),
                "text/plain"
        );

        when(bucketService.downloadFileFromBucket(bucketName, key)).thenReturn(fileDownloaded);

        mockMvc.perform(
                        get("/bucket/download/{bucketName}/{key}/{contentDisposition}", bucketName, key, contentDisposition)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\""))
                .andReturn();
    }

    @Test
    void downloadFileFromBucketWithContentDispositionAttachment() throws Exception {
        String bucketName = "bucket-1";
        String key = "file.txt";
        String contentDisposition = "attachment";

        FileDownloaded fileDownloaded = new FileDownloaded(
                new InputStreamResource(new ByteArrayInputStream("content".getBytes())),
                "text/plain"
        );

        when(bucketService.downloadFileFromBucket(bucketName, key)).thenReturn(fileDownloaded);

        mockMvc.perform(
                        get("/bucket/download/{bucketName}/{key}/{contentDisposition}", bucketName, key, contentDisposition)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\""))
                .andReturn();
    }

    @Test
    void deleteBucketFile() throws Exception {
        String bucketName = "bucket-1";
        String key = "file.txt";

        mockMvc.perform(
                        delete("/bucket/{bucketName}/{key}", bucketName, key)
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(bucketService).deleteFileFromBucket(bucketName, key);
    }
}