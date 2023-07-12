package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ContentDetailsTest {

    @Test
    public void testConstructorWithParameters() {
        String key = "testKey";
        Instant lastModified = Instant.now();
        String eTag = "testETag";
        Long size = 100L;
        String storageClass = "STANDARD";
        Owner owner = Owner.builder()
                .id("testOwnerId")
                .displayName("testOwnerDisplayName")
                .build();

        ContentDetails contentDetails = new ContentDetails(key, lastModified, eTag, size, storageClass, owner);

        assertThat(contentDetails.getKey()).isEqualTo(key);
        assertThat(contentDetails.getLastModified()).isEqualTo(lastModified);
        assertThat(contentDetails.geteTag()).isEqualTo(eTag);
        assertThat(contentDetails.getSize()).isEqualTo(size);
        assertThat(contentDetails.getStorageClass()).isEqualTo(storageClass);
        assertThat(contentDetails.getOwner()).isEqualTo(owner);
    }

    @Test
    public void testConstructorWithS3Object() {
        // Arrange
        S3Object s3Object = S3Object.builder()
                .key("testKey")
                .lastModified(Instant.now())
                .eTag("testETag")
                .size(100L)
                .storageClass("STANDARD")
                .owner(Owner.builder()
                        .id("testOwnerId")
                        .displayName("testOwnerDisplayName")
                        .build())
                .build();

        ContentDetails contentDetails = new ContentDetails(s3Object);

        assertThat(contentDetails.getKey()).isEqualTo(s3Object.key());
        assertThat(contentDetails.getLastModified()).isEqualTo(s3Object.lastModified());
        assertThat(contentDetails.geteTag()).isEqualTo(s3Object.eTag());
        assertThat(contentDetails.getSize()).isEqualTo(s3Object.size());
        assertThat(contentDetails.getStorageClass()).isEqualTo(s3Object.storageClassAsString());
        assertThat(contentDetails.getOwner()).isEqualTo(s3Object.owner());
    }
}
