package com.project.bucketmanager.Utils;

import com.project.bucketmanager.Models.BucketDetails;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class S3ValidationUtilsTest {

    @Test
    void bucketNameMatchRegex_ValidBucketName() {
        String bucketName = "my-bucket-123";
        boolean result = S3ValidationUtils.bucketNameMatchRegex(bucketName);
        assertThat(result).isTrue();
    }

    @Test
    void bucketNameMatchRegex_InvalidBucketName(){
        String bucketName = "my_bucket_123";
        boolean result = S3ValidationUtils.bucketNameMatchRegex(bucketName);
        assertThat(result).isFalse();
    }

    @Test
    void bucketExists_ValidBucketName() {
        List<BucketDetails> buckets = new ArrayList<>();
        buckets.add(new BucketDetails("bucket1", Instant.now()));
        buckets.add(new BucketDetails("bucket2", Instant.now()));
        buckets.add(new BucketDetails("bucket3", Instant.now()));
        String bucketName = "bucket2";

        boolean result = S3ValidationUtils.bucketExists(buckets,bucketName);
        assertThat(result).isTrue();
    }

    @Test
    void bucketExists_InvalidBucketName(){
        List<BucketDetails> buckets = new ArrayList<>();
        buckets.add(new BucketDetails("bucket1", Instant.now()));
        buckets.add(new BucketDetails("bucket2", Instant.now()));
        buckets.add(new BucketDetails("bucket3", Instant.now()));
        String bucketName = "bucket4";

        boolean result = S3ValidationUtils.bucketExists(buckets,bucketName);
        assertThat(result).isFalse();
    }
}