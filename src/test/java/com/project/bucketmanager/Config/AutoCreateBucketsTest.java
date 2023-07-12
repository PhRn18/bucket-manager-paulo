package com.project.bucketmanager.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AutoCreateBucketsTest {
    @Mock
    private Environment environment;
    @Mock
    private S3Client s3Client;
    private AutoCreateBuckets autoCreateBuckets;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testCreateBuckets_WhenAutoCreateBucketsEnabled() {
        autoCreateBuckets = new AutoCreateBuckets(environment,s3Client,true);
        String[] buckets = {"bucket1", "bucket2"};
        ListBucketsResponse bucketList = ListBucketsResponse.builder()
                .buckets(Collections.singletonList(Bucket.builder().name("bucket1").build()))
                .build();
        CreateBucketResponse bucket2CreateBucketResponse = CreateBucketResponse.builder().location("bucket2-location").build();

        when(environment.getRequiredProperty("aws.bucket.names", String[].class))
                .thenReturn(buckets);
        when(s3Client.listBuckets(any(ListBucketsRequest.class)))
                .thenReturn(bucketList);
        when(s3Client.createBucket(any(CreateBucketRequest.class)))
                .thenReturn(bucket2CreateBucketResponse);

        autoCreateBuckets.createBuckets();

        CreateBucketRequest bucket2Builder = CreateBucketRequest.builder().bucket("bucket2").build();
        CreateBucketRequest bucket1Builder = CreateBucketRequest.builder().bucket("bucket1").build();

        verify(s3Client, times(1)).createBucket(bucket2Builder);
        verify(s3Client, never()).createBucket(bucket1Builder);
    }

    @Test
    public void testCreateBuckets_WhenAutoCreateBucketsDisabled() {
        autoCreateBuckets = new AutoCreateBuckets(environment,s3Client,false);

        autoCreateBuckets.createBuckets();

        verify(s3Client, never()).listBuckets(any(ListBucketsRequest.class));
        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }
}