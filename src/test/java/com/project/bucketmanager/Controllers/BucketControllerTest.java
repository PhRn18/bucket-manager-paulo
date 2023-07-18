package com.project.bucketmanager.Controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.*;


@SpringBootTest
class BucketControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Mock
    private BucketService bucketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BucketController bucketController = new BucketController(bucketService);
        mockMvc = MockMvcBuilders.standaloneSetup(bucketController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    void listAllBucketsSuccess() throws Exception {
        BucketDetails bucket1 = new BucketDetails("bucket1", Instant.now());
        BucketDetails bucket2 = new BucketDetails("bucket2", Instant.now());
        List<BucketDetails> buckets = List.of(bucket1,bucket2);
        when(bucketService.listAllBuckets()).thenReturn(buckets);

        MvcResult mvcResult = mockMvc.perform(
                get("/bucket/list")
        ).andExpect(status().isOk()).andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        List<Object> responseBuckets = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(responseBuckets.size()).isNotZero();
    }
    @Test
    void listAllBucketFail() throws Exception {
        List<BucketDetails> buckets = Collections.emptyList();
        when(bucketService.listAllBuckets()).thenReturn(buckets);
        mockMvc.perform(
                get("/bucket/list")
        ).andExpect(status().isNoContent()).andReturn();
    }

    @Test
    void compressFileAndUpdate() throws Exception {
        String bucketName = "mockbucket";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, "Test data".getBytes());

        CompressedFileUpdate expectedResult = new CompressedFileUpdate();
        when(bucketService.compressAndUpdateFileToBucket(eq(multipartFile), eq(bucketName))).thenReturn(expectedResult);

        mockMvc.perform(multipart("/bucket/compress/{bucketName}", bucketName)
                        .file(multipartFile))
                .andExpect(status().isCreated())
                .andReturn();

        verify(bucketService).compressAndUpdateFileToBucket(eq(multipartFile), eq(bucketName));
    }

    @Test
    void searchFileExactMatch() throws Exception {
        String bucketName = "bucketname";
        List<String> keys = List.of("key1","key2");
        SearchFileResult searchFileResult = new SearchFileResult(keys,true,false);
        when(bucketService.searchFile(anyString(),anyString())).thenReturn(searchFileResult);
        MvcResult mvcResult = mockMvc.perform(
                        get("/bucket/search/{bucketName}", bucketName)
                                .queryParam("searchString", "key1")
                ).andExpect(status().isOk())
                .andReturn();
        SearchFileResult responseKeys = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SearchFileResult.class);
        assertThat(responseKeys.getKey().size()).isEqualTo(keys.size());
    }
    @Test
    void searchFilePartialMatch() throws Exception {
        String bucketName = "bucketname";
        List<String> keys = List.of("key1","key2");
        SearchFileResult searchFileResult = new SearchFileResult(keys,false,true);
        when(bucketService.searchFile(anyString(),anyString())).thenReturn(searchFileResult);
        MvcResult mvcResult = mockMvc.perform(
                        get("/bucket/search/{bucketName}", bucketName)
                                .queryParam("searchString", "key")
                ).andExpect(status().isOk())
                .andReturn();
        SearchFileResult responseKeys = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SearchFileResult.class);
        assertThat(responseKeys.getKey().size()).isEqualTo(keys.size());
    }
    @Test
    void listAllFolders() throws Exception {
        String bucketName = "bucket";
        Set<String> folders = new HashSet<>();
        folders.add("folder1");
        folders.add("folder2");
        ListAllFoldersResult listAllFoldersResult = new ListAllFoldersResult(folders,folders.size());
        when(bucketService.listAllFolders(anyString())).thenReturn(listAllFoldersResult);
        MvcResult mvcResult = mockMvc.perform(
                        get("/bucket/folders/{bucketName}", bucketName)
                ).andExpect(status().isOk())
                .andReturn();
        ListAllFoldersResult response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ListAllFoldersResult.class);
        assertThat(response.getNumberOfFolders()).isEqualTo(listAllFoldersResult.getNumberOfFolders());
    }
    @Test
    void listAllFoldersSize() throws Exception {
        String bucketName = "bucketname";

        Map<String,String> folder1 = new HashMap<>();
        folder1.put("key","value1");

        Map<String,String> folder2 = new HashMap<>();
        folder2.put("key","value2");

        List<Map<String,String>> folders = new ArrayList<>();

        folders.add(folder1);
        folders.add(folder2);
        FoldersSize foldersSize = new FoldersSize(folders,folders.size(),30.00);

        when(bucketService.listAllFoldersSize(anyString())).thenReturn(foldersSize);

        MvcResult mvcResult = mockMvc.perform(
                get("/bucket/folders/listSize/{bucketName}", bucketName)
        ).andExpect(status().isOk()).andReturn();

        FoldersSize result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FoldersSize.class);
        assertThat(result.getFolders().size()).isEqualTo(folders.size());
        assertThat(result.getNumberOfFolders()).isEqualTo(folders.size());
    }
    @Test
    void listAllFileExtensions() throws Exception {
        String bucketname = "bucketname";
        Set<String> fileExtensions = new HashSet<>();
        fileExtensions.add("key1");
        fileExtensions.add("key2");
        ListAllFileExtensions listAllFileExtensions = new ListAllFileExtensions(fileExtensions,fileExtensions.size());
        when(bucketService.listAllFileExtensions(anyString())).thenReturn(listAllFileExtensions);
        MvcResult mvcResult = mockMvc.perform(
                get("/bucket/fileExtensions/{bucketName}", bucketname)
        ).andExpect(status().isOk()).andReturn();
        ListAllFileExtensions response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ListAllFileExtensions.class);
        assertThat(response.getFileExtensions().size()).isEqualTo(listAllFileExtensions.getFileExtensions().size());
        assertThat(response.getNumberOfExtensions()).isEqualTo(listAllFileExtensions.getNumberOfExtensions());
    }
    @Test
    void countExtensionOccurrences() throws Exception {
        String bucketname = "bucketname";
        String extension = "extension";
        List<String> fileNames = new ArrayList<>();
        fileNames.add("file1");
        fileNames.add("file2");
        CountExtensionOccurrences countExtensionOccurrences = new CountExtensionOccurrences(extension, fileNames.size(), fileNames);
        when(bucketService.countExtensionOccurrences(anyString(),anyString())).thenReturn(countExtensionOccurrences);
        MvcResult mvcResult = mockMvc.perform(
                get("/bucket/fileExtensions/count/{bucketName}/{extension}", bucketname, extension)
        ).andExpect(status().isOk()).andReturn();
        CountExtensionOccurrences result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CountExtensionOccurrences.class);
        assertThat(result.getFileName().size()).isEqualTo(countExtensionOccurrences.getFileName().size());
        assertThat(result.getOccurrences()).isEqualTo(countExtensionOccurrences.getOccurrences());
        assertThat(result.getExtension()).isEqualTo(countExtensionOccurrences.getExtension());
    }
    @Test
    void moveFileToAnotherBucket() throws Exception {
        String sourceBucket = "sourcebucket";
        String targetBucket = "targetBucket";
        String key = "key";
        doNothing().when(bucketService).moveFileToAnotherBucket(sourceBucket,targetBucket,key);
        mockMvc.perform(
                put("/bucket/move/{sourceBucket}/{targetBucket}",sourceBucket,targetBucket)
                        .queryParam("key",key)
                )
                .andExpect(status().isCreated()).andReturn();
        verify(bucketService).moveFileToAnotherBucket(anyString(),anyString(),anyString());
    }
    @Test
    void listAllBucketContent() throws Exception {
        String bucketName = "bucket-name";
        BucketContent bucketContent = new BucketContent(List.of(
                new Content("fdsdfsfds","312321213",100L),
                new Content("grgr","324132321",200L),
                new Content("1231dsas","fadfda",300L)
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

        mockMvc.perform(get("/bucket/details/{bucketName}", bucketName)
                        .queryParam("key",key))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }



    @Test
    void uploadFileToBucket() throws Exception {
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
                "text/plain",
                key
        );

        when(bucketService.downloadFileFromBucket(bucketName, key)).thenReturn(fileDownloaded);

        mockMvc.perform(
                        get("/bucket/download/{bucketName}", bucketName)
                                .queryParam("key",key)
                                .queryParam("contentDisposition",contentDisposition)
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
                "text/plain",
                key
        );

        when(bucketService.downloadFileFromBucket(bucketName, key)).thenReturn(fileDownloaded);

        mockMvc.perform(
                        get("/bucket/download/{bucketName}", bucketName)
                                .queryParam("key",key)
                                .queryParam("contentDisposition",contentDisposition)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\""))
                .andReturn();
    }

    @Test
    void generateFileUrl() throws Exception {
        String bucketName = "bucket-name";
        String key = "file/sampleFile.json";
        String expirationTime = "60"; //minutes

        String mockUrl = "aws.s3.amazonaws.com";

        when(bucketService.generateFileUrl(bucketName,key,expirationTime)).thenReturn(mockUrl);

        MvcResult mvcResult = mockMvc.perform(
                        get("/bucket/url/{bucketName}/{expirationTime}", bucketName, expirationTime)
                                .queryParam("key", key)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertEquals(responseBody,mockUrl);

    }

    @Test
    void searchFileByCharSequence() throws Exception {
        String bucketName = "bucket-name1";
        String searchString = ".png";

        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");

        SearchFileResult mockSearchFileResult = new SearchFileResult(keys,true,false);

        when(bucketService.searchFile(anyString(),anyString())).thenReturn(mockSearchFileResult);

        mockMvc.perform(
                get("/bucket/{bucketName}", bucketName)
                        .queryParam("searchString", searchString)
        )
                .andExpect(status().isOk())
                .andReturn();
    }



    @Test
    void deleteBucketFile() throws Exception {
        String bucketName = "bucket-1";
        String key = "file.txt";

        mockMvc.perform(
                        delete("/bucket/{bucketName}", bucketName)
                                .queryParam("key",key)
                )
                .andExpect(status().isCreated())
                .andReturn();

        verify(bucketService).deleteFileFromBucket(bucketName, key);
    }
}