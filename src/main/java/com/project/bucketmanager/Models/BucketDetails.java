package com.project.bucketmanager.Models;

import software.amazon.awssdk.services.s3.model.Bucket;

import java.time.Instant;

public class BucketDetails {

    private String bucketName;
    private Instant creationDate;

    public BucketDetails() {
    }

    public BucketDetails(String bucketName, Instant creationDate) {
        this.bucketName = bucketName;
        this.creationDate = creationDate;
    }

    public BucketDetails(Bucket bucket){
        this.bucketName=bucket.name();
        this.creationDate = bucket.creationDate();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }


    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }
}
