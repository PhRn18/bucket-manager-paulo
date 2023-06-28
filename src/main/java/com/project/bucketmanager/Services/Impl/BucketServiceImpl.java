package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.ExceptionHandler.Exceptions.*;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import com.project.bucketmanager.Services.SnsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BucketServiceImpl implements BucketService {
    private final S3Client s3Client;
    private final SnsService snsService;

    private final S3Presigner s3Presigner;
    public BucketServiceImpl(S3Client s3Client,SnsService snsService,S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.snsService = snsService;
        this.s3Presigner = s3Presigner;
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
        if(file.isEmpty()){
            throw new EmptyFileException("Empty file!");
        }

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
                snsService.notifyFileUploaded("File uploaded to bucket : "+bucketName);
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
    public String generateFileUrl(String bucketName, String key,String expirationTime) {
        validateStringParam(bucketName);
        validateStringParam(key);

        GetObjectRequest getObjectRequest = getObjectRequest(bucketName, key);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(Long.parseLong(expirationTime)))
                .getObjectRequest(getObjectRequest)
                .build();

        try{
            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        }catch (S3Exception e){
            throw new PresignUrlException("Unable to generate the file url");
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
            snsService.notifyFileDeleted("File name: "+key + "deleted!");
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
