package com.project.bucketmanager.Models;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BucketContentTest {

    @Test
    public void testConstructorWithParameters() {
        List<Content> contentList = List.of(
                new Content("key1", "etag1",100L),
                new Content("key2", "etag2",200L)
        );

        BucketContent bucketContent = new BucketContent(contentList);

        assertThat(bucketContent.getObjectList()).isEqualTo(contentList);
    }

    @Test
    public void testConstructorWithoutParameters() {
        List<Content> emptyList = Collections.emptyList();

        BucketContent bucketContent = new BucketContent();

        assertThat(bucketContent.getObjectList()).isEqualTo(emptyList);
    }
}
