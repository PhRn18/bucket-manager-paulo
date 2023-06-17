package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.Content;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Services.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class BucketServiceImplTest {
    @Mock
    private S3Client s3Client;
    private BucketService bucketService;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        bucketService = new BucketServiceImpl(s3Client);
    }
    @Test
    void listAllBucketContent() {
        String bucketName = "bucket-1";
        List<S3Object> objectList = List.of(
            createS3Object("key1","etag1"),
            createS3Object("key2","etag2"),
            createS3Object("key3","etag3")
        );

        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(objectList)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        BucketContent bucketContent = bucketService.listAllBucketContent(bucketName);

        List<Content> expectedContentList = objectList
                .stream()
                .map(Content::new)
                .collect(Collectors.toList());

        BucketContent expectedBucketContent = new BucketContent(expectedContentList);

        assertThat(bucketContent.getObjectList()).isNotNull();
        assertThat(expectedBucketContent.getObjectList()).isNotNull();
        assertThat(expectedBucketContent.getObjectList().size()).isEqualTo(bucketContent.getObjectList().size());

    }
    @Test
    void getBucketContentDetailsByKey() {
        String bucketName = "bucket-name";
        S3Object s3Object1 = createS3Object("key1", "etag1");
        S3Object s3Object2 = createS3Object("key2", "etag2");
        S3Object s3Object3 = createS3Object("key3", "etag3");
        List<S3Object> objectList = List.of(
                s3Object1,
                s3Object2,
                s3Object3
        );

        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(objectList)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        ContentDetails contentDetails1 = bucketService.getBucketContentDetailsByKey(bucketName,"key1");
        ContentDetails contentDetails2 = bucketService.getBucketContentDetailsByKey(bucketName,"key2");
        ContentDetails contentDetails3 = bucketService.getBucketContentDetailsByKey(bucketName,"key3");

        assertThat(contentDetails1.geteTag()).isEqualTo(s3Object1.eTag());
        assertThat(contentDetails2.geteTag()).isEqualTo(s3Object2.eTag());
        assertThat(contentDetails3.geteTag()).isEqualTo(s3Object3.eTag());
    }


    private static S3Object createS3Object(String key,String eTag){
        return S3Object
                .builder()
                .eTag(eTag)
                .key(key)
                .lastModified(Instant.now())
                .size(0L)
                .storageClass(ObjectStorageClass.STANDARD)
                .owner(Owner.builder().id("owner-id").displayName("owner-name").build())
                .build();
    }
}