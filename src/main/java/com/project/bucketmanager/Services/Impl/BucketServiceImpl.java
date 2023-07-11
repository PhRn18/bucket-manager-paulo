package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.Config.AutoCreateBuckets;
import com.project.bucketmanager.ExceptionHandler.Exceptions.*;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import com.project.bucketmanager.Services.SnsService;
import com.project.bucketmanager.Utils.FileUtil;
import com.project.bucketmanager.Validation.ValidateMultipartFile;
import com.project.bucketmanager.Validation.ValidateStringParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static com.project.bucketmanager.Utils.FileUtil.*;
import static com.project.bucketmanager.Utils.GzipUtils.getCompressedBytesUsingGZIP;
import static com.project.bucketmanager.Utils.S3ServiceHelper.*;

@Service
public class BucketServiceImpl implements BucketService {
    private static final Logger logger = LoggerFactory.getLogger(AutoCreateBuckets.class);
    private final S3Client s3Client;
    private final SnsService snsService;

    private final S3Presigner s3Presigner;
    public BucketServiceImpl(S3Client s3Client,SnsService snsService,S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.snsService = snsService;
        this.s3Presigner = s3Presigner;
    }
    @Override
    @Cacheable("cachedBucketList")
    public List<BucketDetails> listAllBuckets() {
        try{
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
            return listBucketsResponse
                    .buckets()
                    .stream()
                    .map(BucketDetails::new)
                    .toList();
        }catch (S3Exception e){
            logger.error("[BucketService]-ERROR listing all buckets");
            throw new ListAllBucketsException("Unable to list all buckets");
        }
    }
    @Override
    @Cacheable("cachedBucketContent")
    @ValidateStringParams
    public BucketContent listAllBucketContent(String bucketName) {
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
        }catch (S3Exception ex){
            logger.error("[BucketService]-ERROR listing all bucket content");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @Cacheable("cachedContentDetails")
    @ValidateStringParams
    public ContentDetails getBucketContentDetailsByKey(String bucketName,String key) {
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
            logger.error("[BucketService]-ERROR getting bucket content details by key");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @CacheEvict(value = "cachedBucketContent", allEntries = true)
    @ValidateStringParams
    @ValidateMultipartFile
    public void updateFileToBucket(MultipartFile file, String bucketName) {
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
            logger.error("[BucketService]-ERROR while updating file to bucket");
            throw new FileUploadException("Unable to upload the file: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "cachedBucketContent", allEntries = true)
    @ValidateStringParams
    @ValidateMultipartFile
    public CompressedFileUpdate compressAndUpdateFileToBucket(MultipartFile file, String bucketName) {
        String fileName = file.getOriginalFilename();
        String compressedFileName = getFileNameWithoutExtension(fileName)+"-"+getExtensionFromKey(fileName)+".gz";
        try (InputStream inputStream = file.getInputStream()) {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(compressedFileName)
                    .build();
            try {
                s3Client.headObject(headObjectRequest);
                throw new FileAlreadyExistsException("File already exists in the bucket: " + compressedFileName);
            } catch (NoSuchKeyException e) {
                byte[] compressedBytes = getCompressedBytesUsingGZIP(inputStream);

                try (ByteArrayInputStream compressedInputStream = new ByteArrayInputStream(compressedBytes)) {
                    int compressedFileSize = compressedBytes.length;
                    RequestBody fileInputStream = RequestBody.fromInputStream(compressedInputStream, compressedFileSize);
                    s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(compressedFileName)
                            .contentType("application/gzip")
                            .build(), fileInputStream);
                    snsService.notifyFileUploaded("File uploaded to bucket: " + bucketName);
                    return new CompressedFileUpdate(compressedFileName,file.getSize(), compressedFileSize);
                }
            }
        } catch (IOException e) {
            logger.error("[BucketService]-ERROR while compressing and updating file to bucket");
            throw new FileUploadException("Unable to upload the file: " + e.getMessage());
        }
    }

    @Override
    @ValidateStringParams
    public FileDownloaded downloadFileFromBucket(String bucketName, String key) {
        GetObjectRequest getObjectRequest = getObjectRequest(bucketName, key);
        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            String contentType = responseInputStream.response().contentType();
            InputStreamResource inputStreamResource;
            String originalFileExtension = getOriginalFileExtension(key);
            if("application/gzip".equals(contentType)){
                String oldFileName = key.split("-")[0]+"."+originalFileExtension;
                GZIPInputStream gzipInputStream = new GZIPInputStream(responseInputStream);
                inputStreamResource = new InputStreamResource(gzipInputStream);
                contentType = "application/"+originalFileExtension;
                return new FileDownloaded(inputStreamResource,contentType,oldFileName);
            }else{
                inputStreamResource = new InputStreamResource(responseInputStream);
                return new FileDownloaded(inputStreamResource,contentType,key);
            }
        } catch (Exception e) {
            logger.error("[BucketService]-ERROR downloading file from bucket");
            throw new FileDownloadException("Unable to download S3 file!.", e);
        }
    }

    @Override
    @ValidateStringParams
    public String generateFileUrl(String bucketName, String key,String expirationTime) {
        GetObjectRequest getObjectRequest = getObjectRequest(bucketName, key);

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(Long.parseLong(expirationTime)))
                .getObjectRequest(getObjectRequest)
                .build();

        try{
            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        }catch (S3Exception e){
            logger.error("[BucketService]-ERROR generating file url");
            throw new PresignUrlException("Unable to generate the file url");
        }
    }

    @Override
    @ValidateStringParams
    public SearchFileResult searchFile(String bucketName, String searchString) {
        ListObjectsV2Request request = getListObjectsV2Request(bucketName);

        try{
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(request);
            if(listObjectsV2Response!=null){
                List<S3Object> keys = listObjectsV2Response.contents();
                List<S3Object> filteredFiles = keys
                        .stream()
                        .filter(s3Object -> s3Object.key().contains(searchString))
                        .toList();

                if(filteredFiles.isEmpty()){
                    throw new ContentNotFoundException("Unable to find file with key: "+searchString);
                }

                List<String> fileKeys = filteredFiles.stream().map(S3Object::key).toList();
                boolean singleResult = fileKeys.size() == 1;
                boolean multipleResults = fileKeys.size() > 1;
                return new SearchFileResult(fileKeys,singleResult,multipleResults);
            }
            throw new ContentNotFoundException("Unable to find file with key: "+searchString);
        }catch (S3Exception ex){
            logger.error("[BucketService]-ERROR searching file");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @ValidateStringParams
    public ListAllFoldersResult listAllFolders(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);
        try {
            List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();

            if (contents.isEmpty()) {
                return ListAllFoldersResult.buildEmptyResponse();
            }

            Set<String> foldersSet = contents
                    .stream()
                    .map(S3Object::key)
                    .map(FileUtil::getFolderFromKey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            return new ListAllFoldersResult(foldersSet, foldersSet.size());
        } catch (S3Exception e) {
            logger.error("[BucketService]-ERROR listing all bucket folders");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @ValidateStringParams
    public FoldersSize listAllFoldersSize(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);
        try {
            List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();

            if (contents.isEmpty()) {
                return FoldersSize.buildEmptyResponse();
            }

            FoldersSize foldersSize = new FoldersSize();

            contents.forEach(s3Object -> {
                String objectKey = s3Object.key();
                String folder = getFolderFromKey(objectKey);
                if (folder != null) {
                    double objectSize = s3Object.size();
                    foldersSize.addFolder(folder, String.valueOf(objectSize));
                    foldersSize.setTotalSize(foldersSize.getTotalSize() + objectSize);
                }
            });

            foldersSize.setNumberOfFolders(foldersSize.getFolders().size());

            return foldersSize;
        } catch (S3Exception e) {
            logger.error("[BucketService]-ERROR listing all folders size");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @ValidateStringParams
    public ListAllFileExtensions listAllFileExtensions(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);
        try {
            List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();

            if (contents.isEmpty()) {
                return ListAllFileExtensions.buildEmptyResponse();
            }

            Set<String> extensionsSet = contents
                    .stream()
                    .map(S3Object::key)
                    .map(FileUtil::getExtensionFromKey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            return new ListAllFileExtensions(extensionsSet, extensionsSet.size());
        } catch (S3Exception e) {
            logger.error("[BucketService]-ERROR listing all file extensions");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @ValidateStringParams
    public CountExtensionOccurrences countExtensionOccurrences(String bucketName, String extension) {
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);
        try {
            List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();

            if (contents.isEmpty()) {
                return CountExtensionOccurrences.buildEmptyResponse();
            }

            List<String> extensionOccurrences = new ArrayList<>();
            contents.stream()
                    .map(S3Object::key)
                    .forEach(key -> {
                        String fileExtension = getExtensionFromKey(key);
                        if (fileExtension != null && fileExtension.equalsIgnoreCase(extension)) {
                            extensionOccurrences.add(key);
                        }
                    });

            return new CountExtensionOccurrences(extension, extensionOccurrences.size(),extensionOccurrences);
        } catch (S3Exception e) {
            logger.error("[BucketService]-ERROR couting extensions occurrences");
            throw new IllegalArgumentException("Bucket does not exist: " + bucketName);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cachedBucketContent", allEntries = true),
            @CacheEvict(value = "cachedContentDetails", allEntries = true)
    })
    @ValidateStringParams
    public void moveFileToAnotherBucket(String sourceBucket, String targetBucket, String key) {
        CopyObjectRequest copyObjectRequest = getCopyObjectRequest(sourceBucket, targetBucket, key);
        DeleteObjectRequest deleteObjectRequest = getDeleteObjectRequest(sourceBucket,key);
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(sourceBucket);
        try{
            s3Client.copyObject(copyObjectRequest);
            performDeleteObject(s3Client,key,deleteObjectRequest,listObjectsV2Request);
        }catch (S3Exception e){
            logger.error("[BucketService]-ERROR moving file");
            throw new CopyFileException("Unable to move file from "+sourceBucket+" to "+targetBucket);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "cachedBucketContent", allEntries = true),
            @CacheEvict(value = "cachedContentDetails", allEntries = true)
    })
    @ValidateStringParams
    public void deleteFileFromBucket(String bucketName, String key) {
        DeleteObjectRequest deleteObjectRequest = getDeleteObjectRequest(bucketName,key);
        ListObjectsV2Request listObjectsV2Request = getListObjectsV2Request(bucketName);

        try {
            performDeleteObject(s3Client,key, deleteObjectRequest, listObjectsV2Request);
            snsService.notifyFileDeleted("File name: "+key + "deleted!");
        } catch (S3Exception e) {
            logger.error("[BucketService]-ERROR deleting file");
            throw new FileDeleteException("Unable to delete file from S3 bucket!", e);
        }
    }

}
