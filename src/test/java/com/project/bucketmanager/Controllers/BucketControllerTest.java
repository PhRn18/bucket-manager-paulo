package com.project.bucketmanager.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.Content;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

        mockMvc.perform(get("/bucket/{bucketName}/{key}", bucketName, key))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}