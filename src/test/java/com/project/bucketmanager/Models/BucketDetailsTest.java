package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class BucketDetailsTest {
    @Test
    void testDefaultConstructor(){
        String bucketName = "bucket-1";
        Instant creationDate = Instant.now();
        BucketDetails bucketDetails = new BucketDetails(bucketName,creationDate);

        assertThat(bucketDetails.getBucketName()).isEqualTo(bucketName);
        assertThat(bucketDetails.getCreationDate()).isEqualTo(creationDate);
    }
    @Test
    void testBucketConstructor(){
        String bucketName = "bucket-1";
        Instant creationDate = Instant.now();
        Bucket bucket = Bucket.builder()
                .name(bucketName)
                .creationDate(creationDate)
                .build();

        assertThat(bucket.creationDate()).isEqualTo(creationDate);
        assertThat(bucket.name()).isEqualTo(bucketName);
    }

}