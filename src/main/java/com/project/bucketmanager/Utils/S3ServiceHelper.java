package com.project.bucketmanager.Utils;

import com.project.bucketmanager.ExceptionHandler.Exceptions.ContentNotFoundException;
import com.project.bucketmanager.ExceptionHandler.Exceptions.FileDeleteException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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
    public static CopyObjectRequest getCopyObjectRequest(String sourceBucket, String targetBucket, String key){
        return CopyObjectRequest
                .builder()
                .sourceBucket(sourceBucket)
                .sourceKey(key)
                .destinationBucket(targetBucket)
                .destinationKey(key)
                .build();
    }

    public static void performDeleteObject(S3Client s3Client, String key, DeleteObjectRequest deleteObjectRequest, ListObjectsV2Request listObjectsV2Request) throws S3Exception{
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        if(listObjectsV2Response==null){
            throw new ContentNotFoundException("Unable to list any files - S3 connection ERROR!");
        }
        listObjectsV2Response
                .contents()
                .stream()
                .filter(s3Object -> s3Object.key().equals(key))
                .findFirst()
                .orElseThrow(()->new FileDeleteException("Unable to delete file from S3 bucket - File not found"));

        s3Client.deleteObject(deleteObjectRequest);
    }
}
