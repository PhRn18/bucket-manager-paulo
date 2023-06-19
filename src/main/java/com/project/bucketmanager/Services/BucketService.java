package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Models.FileDownloaded;
import org.springframework.web.multipart.MultipartFile;

public interface BucketService {
    BucketContent listAllBucketContent(String bucketName);
    ContentDetails getBucketContentDetailsByKey(String bucketName,String key);
    void updateFileToBucket(MultipartFile file,String bucketName);
    FileDownloaded downloadFileFromBucket(String bucketName, String key);
    void deleteFileFromBucket(String bucketName,String key);
}
