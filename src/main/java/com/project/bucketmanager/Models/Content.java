package com.project.bucketmanager.Models;

import software.amazon.awssdk.services.s3.model.S3Object;

public class Content {
    private final String key;
    private final String eTag;
    private final long size;

    public Content(String key, String eTag, long size) {
        this.key = key;
        this.eTag = eTag;
        this.size = size;
    }

    public Content(S3Object s3Object){
        this.eTag=s3Object.eTag();
        this.key=s3Object.key();
        this.size= s3Object.size();
    }

    public String getKey() {
        return key;
    }

    public String getETag() {
        return eTag;
    }
    public long getSize(){
        return size;
    }
}
