package com.project.bucketmanager.Services;

import com.project.bucketmanager.Models.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BucketService {
    List<BucketDetails> listAllBuckets();
    BucketContent listAllBucketContent(String bucketName);
    ContentDetails getBucketContentDetailsByKey(String bucketName,String key);
    void updateFileToBucket(MultipartFile file,String bucketName);
    FileDownloaded downloadFileFromBucket(String bucketName, String key);
    String generateFileUrl(String bucketName,String key,String expirationTime);
    SearchFileResult searchFile(String bucketName,String searchString);
    ListAllFoldersResult listAllFolders(String bucketName);
    ListAllFileExtensions listAllFileExtensions(String bucketName);
    CountExtensionOccurrences countExtensionOccurrences(String bucketName,String extension);
    void deleteFileFromBucket(String bucketName,String key);
}
