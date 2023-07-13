package com.project.bucketmanager.Models;

import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;

public class ContentDetails {
    private String key;
    private Instant lastModified;
    private String eTag;
    private Long size;
    private String storageClass;
    private Owner owner;

    public ContentDetails(String key, Instant lastModified, String eTag, Long size, String storageClass, Owner owner) {
        this.key = key;
        this.lastModified = lastModified;
        this.eTag = eTag;
        this.size = size;
        this.storageClass = storageClass;
        this.owner = owner;
    }
    public ContentDetails(S3Object s3Object){
        this.eTag=s3Object.eTag();
        this.key=s3Object.key();
        this.lastModified=s3Object.lastModified();
        this.size=s3Object.size();
        this.storageClass=s3Object.storageClassAsString();
        this.owner=s3Object.owner();
    }

    public ContentDetails() {
    }

    public String getKey() {
        return key;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public String geteTag() {
        return eTag;
    }

    public Long getSize() {
        return size;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public Owner getOwner() {
        return owner;
    }
    public static ContentDetails buildEmptyResponse(){
        return new ContentDetails();
    }
}
