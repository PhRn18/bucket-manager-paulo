package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Models.FileDownloaded;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface BucketService {
    public BucketContent listAllBucketContent(String bucketName);
    public ContentDetails getBucketContentDetailsByKey(String bucketName,String key);
    public void updateFileToBucket(MultipartFile file,String bucketName);
    public FileDownloaded downloadFileFromBucket(String bucketName, String key);
    public void deleteFileFromBucket(String bucketName,String key);
}
