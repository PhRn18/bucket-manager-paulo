package com.project.bucketmanager.Models;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.S3Object;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
class ContentTest {

    @Test
    void testBasicConstructor(){
        String key = "mock-key-123";
        String etag = "mock-etag-12323321231";
        Content content = new Content(key,etag);
        assertThat(content.getETag()).isEqualTo(etag);
        assertThat(content.getKey()).isEqualTo(key);
    }
    @Test
    void testS3ObjectConstructor(){
        String etag = "123321321321";
        String key = "123321321321";

        S3Object s3Object = S3Object
                .builder()
                .key(key)
                .eTag(etag)
                .build();

        Content content = new Content(s3Object);

        assertThat(content.getETag()).isEqualTo(etag);
        assertThat(content.getKey()).isEqualTo(key);

    }
}