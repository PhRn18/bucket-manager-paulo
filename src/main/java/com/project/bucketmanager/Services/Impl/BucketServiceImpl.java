package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.ExceptionHandler.Exceptions.*;
import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.Content;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Models.FileDownloaded;
import com.project.bucketmanager.Services.BucketService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BucketServiceImpl implements BucketService {
    private final S3Client s3Client;
    public BucketServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    @Override
    @Cacheable("cachedBucketContent")
    public BucketContent listAllBucketContent(String bucketName) {
        validateStringParam(bucketName);
        ListObjectsV2Request request = getListObjectsV2Request(bucketName);

        try{
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(request);
            if(listObjectsV2Response!=null){
                List<Content> keys = listObjectsV2Response
                        .contents()
                        .stream()
                        .map(Content::new)
                        .collect(Collectors.toList());
                return new BucketContent(keys);
            }
            return new BucketContent();
        }catch (NoSuchBucketException ex){
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @Cacheable("cachedContentDetails")
    public ContentDetails getBucketContentDetailsByKey(String bucketName,String key) {
        validateStringParam(bucketName);
        ListObjectsV2Request request = getListObjectsV2Request(bucketName);

        try{
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(request);
            if(listObjectsV2Response!=null){
                return listObjectsV2Response
                        .contents()
                        .stream()
                        .filter(s3Object -> s3Object.key().equals(key))
                        .map(ContentDetails::new)
                        .findFirst()
                        .orElseThrow(()->new ContentNotFoundException("Content with key "+key+" not found!"));
            }
            return new ContentDetails();
        }catch (NoSuchBucketException ex){
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @CacheEvict(value = "cachedBucketContent", allEntries = true)
    public void updateFileToBucket(MultipartFile file, String bucketName) {
        validateStringParam(bucketName);

        String fileName = file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            try {
                s3Client.headObject(headObjectRequest);
                throw new FileAlreadyExistsException("File already exists in the bucket: " + fileName);
            } catch (NoSuchKeyException e) {
                RequestBody fileInputStream = RequestBody.fromInputStream(inputStream, file.getSize());
                s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(), fileInputStream);
            }
        } catch (IOException e) {
            throw new FileUploadException("Unable to upload the file: " + e.getMessage());
        }
    }

    @Override
    public FileDownloaded downloadFileFromBucket(String bucketName, String key) {
        validateStringParam(bucketName);
        validateStringParam(key);
        GetObjectRequest getObjectRequest = getObjectRequest(bucketName, key);
        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            String contentType = responseInputStream.response().contentType();
            InputStreamResource inputStreamResource = new InputStreamResource(responseInputStream);
            return new FileDownloaded(inputStreamResource,contentType);
        } catch (Exception e) {
            throw new FileDownloadException("Unable to download S3 file!.", e);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cachedBucketContent", allEntries = true),
            @CacheEvict(value = "cachedContentDetails", allEntries = true)
    })
    public void deleteFileFromBucket(String bucketName, String key) {
        validateStringParam(bucketName);
        validateStringParam(key);

        DeleteObjectRequest deleteObjectRequest = getDeleteObjectRequest(bucketName,key);
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);

        try {
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
        } catch (S3Exception e) {
            throw new FileDeleteException("Unable to delete file from S3 bucket!", e);
        }
    }

    private static GetObjectRequest getObjectRequest(String bucketName,String key){
        return GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }
    private static DeleteObjectRequest getDeleteObjectRequest(String bucketName,String key){
        return DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }
    private static ListObjectsV2Request getListObjectsV2Request(String bucketName) {
        return ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
    }
    private static void validateStringParam(String param) {
        boolean invalidBucketName = param == null || param.isEmpty() ;
        if (invalidBucketName) {
            throw new IllegalArgumentException("Param must not be null or empty");
        }
    }

}
