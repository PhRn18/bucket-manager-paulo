package com.project.bucketmanager.Utils;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

public class S3ServiceHelper {
    public static GetObjectRequest getObjectRequest(String bucketName, String key){
        return GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }
    public static DeleteObjectRequest getDeleteObjectRequest(String bucketName, String key){
        return DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }
    public static ListObjectsV2Request getListObjectsV2Request(String bucketName) {
        return ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
    }
}
