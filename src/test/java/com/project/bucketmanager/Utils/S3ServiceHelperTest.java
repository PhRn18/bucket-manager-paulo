package com.project.bucketmanager.Utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class S3ServiceHelperTest {
    @Mock
    private S3Client s3Client;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getObjectRequest() {
        String mockBucketName = "bucket-1";
        String mockKey = "mock-key";
        GetObjectRequest getObjectRequest = S3ServiceHelper.getObjectRequest(mockBucketName,mockKey);

        assertThat(getObjectRequest).isNotNull();
        assertThat(getObjectRequest.key()).isEqualTo(mockKey);
        assertThat(getObjectRequest.bucket()).isEqualTo(mockBucketName);
    }

    @Test
    void getDeleteObjectRequest() {
        String bucketName = "testBucket";
        String key = "testKey";
        DeleteObjectRequest deleteObjectRequest = S3ServiceHelper.getDeleteObjectRequest(bucketName, key);

        assertThat(deleteObjectRequest).isNotNull();
        assertThat(deleteObjectRequest.bucket()).isEqualTo(bucketName);
        assertThat(deleteObjectRequest.key()).isEqualTo(key);
    }

    @Test
    void getListObjectsV2Request() {
        String bucketName = "testBucket";
        ListObjectsV2Request listObjectsV2Request = S3ServiceHelper.getListObjectsV2Request(bucketName);

        assertThat(listObjectsV2Request).isNotNull();
        assertThat(listObjectsV2Request.bucket()).isEqualTo(bucketName);
    }

    @Test
    void getCopyObjectRequest() {
        String sourceBucket = "sourceBucket";
        String targetBucket = "targetBucket";
        String key = "testKey";
        CopyObjectRequest copyObjectRequest = S3ServiceHelper.getCopyObjectRequest(sourceBucket, targetBucket, key);

        assertThat(copyObjectRequest).isNotNull();
        assertThat(copyObjectRequest.sourceBucket()).isEqualTo(sourceBucket);
        assertThat(copyObjectRequest.sourceKey()).isEqualTo(key);
        assertThat(copyObjectRequest.destinationBucket()).isEqualTo(targetBucket);
        assertThat(copyObjectRequest.destinationKey()).isEqualTo(key);
    }

    @Test
    void performDeleteObject(){
        String key = "key1";
        String sourceBucket = "bucketname";
        DeleteObjectRequest deleteObjectRequest = S3ServiceHelper.getDeleteObjectRequest(sourceBucket, key);
        ListObjectsV2Request listObjectsV2Request = S3ServiceHelper.getListObjectsV2Request(sourceBucket);
        S3Object s3Object = S3Object.builder().key(key).build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder().contents(s3Object).build();

        when(s3Client.listObjectsV2(listObjectsV2Request)).thenReturn(listObjectsV2Response);

        S3ServiceHelper.performDeleteObject(s3Client, key, deleteObjectRequest, listObjectsV2Request);

        verify(s3Client).listObjectsV2(listObjectsV2Request);
        verify(s3Client).deleteObject(deleteObjectRequest);
    }
}