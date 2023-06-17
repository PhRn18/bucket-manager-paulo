package com.project.bucketmanager.Models;

import software.amazon.awssdk.services.s3.model.S3Object;

public class Content {
    private final String key;
    private final String eTag;

    public Content(String key, String eTag) {
        this.key = key;
        this.eTag = eTag;
    }

    public Content(S3Object s3Object){
        this.eTag=s3Object.eTag();
        this.key=s3Object.key();
    }

    public String getKey() {
        return key;
    }

    public String getETag() {
        return eTag;
    }
}
