package com.project.bucketmanager.Services.Impl;

import com.project.bucketmanager.ExceptionHandler.Exceptions.*;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import com.project.bucketmanager.Services.SnsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.bucketmanager.Utils.GzipUtils.getCompressedBytesUsingGZIP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BucketServiceImplTest {
    @Mock
    private S3Client s3Client;
    @Mock
    private SnsService snsService;
    @Mock
    private S3Presigner s3Presigner;
    private BucketService bucketService;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        bucketService = new BucketServiceImpl(s3Client,snsService,s3Presigner);
    }
    @Test
    void listAllBuckets(){
        Bucket bucket1 = Bucket.builder().name("bucket1").creationDate(Instant.now()).build();
        Bucket bucket2 = Bucket.builder().name("bucket2").creationDate(Instant.now()).build();
        List<Bucket> buckets = List.of(bucket1,bucket2);
        ListBucketsResponse listBucketsResponse = ListBucketsResponse.builder().buckets(buckets).build();
        when(s3Client.listBuckets()).thenReturn(listBucketsResponse);
        List<BucketDetails> list = bucketService.listAllBuckets();
        assertThat(list.size()).isEqualTo(buckets.size());
    }
    @Test
    void listAllBucketsThrowingS3Exception(){
        when(s3Client.listBuckets()).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.listAllBuckets()).isInstanceOf(ListAllBucketsException.class);
    }
    @Test
    void compressAndUpdateFileToBucket(){
        String bucketName = "myBucket";
        String originalFileName = "file.txt";
        byte[] fileContent = "Hello, World!".getBytes();
        int fileSize = fileContent.length;

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                originalFileName,
                "text/plain",
                fileContent
        );

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.class);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        CompressedFileUpdate compressedFileUpdate = bucketService.compressAndUpdateFileToBucket(multipartFile, bucketName);

        verify(s3Client).headObject(any(HeadObjectRequest.class));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        verify(snsService).notifyFileUploaded("File uploaded to bucket: " + bucketName);

        assertThat(compressedFileUpdate.getFileName()).isEqualTo("file-txt.gz");
        assertThat(compressedFileUpdate.getOriginalFileSize()).isEqualTo(fileSize);
        assertThat(compressedFileUpdate.getCompressedFileSize()).isEqualTo(33L);
    }
    @Test
    void compressAndUpdateFileToBucketThrowingFileAlreadyExistsException(){
        String bucketName = "myBucket";
        String originalFileName = "file.txt";
        byte[] fileContent = "Hello, World!".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                originalFileName,
                "text/plain",
                fileContent
        );

        assertThatThrownBy(()->bucketService.compressAndUpdateFileToBucket(multipartFile,bucketName))
                .isInstanceOf(FileAlreadyExistsException.class);
    }
    @Test
    void compressAndUpdateFileToBucketThrowingS3Exception(){
        String bucketName = "myBucket";
        String originalFileName = "file.txt";
        byte[] fileContent = "Hello, World!".getBytes();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                originalFileName,
                "text/plain",
                fileContent
        );

        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.class);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().build());

        assertThatThrownBy(()->bucketService.compressAndUpdateFileToBucket(multipartFile,bucketName)).isInstanceOf(FileUploadException.class);
    }
    @Test
    void downloadNormalFileFromBucket(){
        String bucketName = "mockbucketname";
        String key = "mockfile.txt";
        byte[] mockByteArrayInputStream = "content of mockfile".getBytes();
        ResponseInputStream<GetObjectResponse> responseInputStream =new ResponseInputStream<>(
                GetObjectResponse.builder().contentType("text/plain").build(),
                AbortableInputStream.create(new ByteArrayInputStream(mockByteArrayInputStream))
        );
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);
        InputStreamResource expectedInputStreamResource = new InputStreamResource(responseInputStream);
        FileDownloaded expectedFileDownloaded = new FileDownloaded(expectedInputStreamResource,"text/plain",key);
        FileDownloaded responseFileDownloaded = bucketService.downloadFileFromBucket(bucketName,key);
        assertThat(expectedFileDownloaded.getInputStreamResource()).isEqualTo(responseFileDownloaded.getInputStreamResource());
        assertThat(expectedFileDownloaded.getFileName()).isEqualTo(responseFileDownloaded.getFileName());
        assertThat(expectedFileDownloaded.getContentType()).isEqualTo(responseFileDownloaded.getContentType());
    }
    @Test
    void downloadFileFromBucketThrowingS3Exception(){
        String bucketName = "mockbucketname";
        String key = "mockfile.txt";
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(new RuntimeException());

        assertThatThrownBy(()->bucketService.downloadFileFromBucket(bucketName,key))
                .isInstanceOf(FileDownloadException.class);
    }
    @Test
    void downloadGzipFileFromBucket() throws IOException {
        String bucketName = "mockbucketname";
        String key = "mockfile-txt.gz";
        MultipartFile multipartFile = new MockMultipartFile(key,key.getBytes());
        ResponseInputStream<GetObjectResponse> responseInputStream =new ResponseInputStream<>(
                GetObjectResponse.builder().contentType("application/gzip").build(),
                AbortableInputStream.create(new ByteArrayInputStream(getCompressedBytesUsingGZIP(multipartFile.getInputStream())))
        );
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);
        InputStreamResource inputStreamResource = new InputStreamResource(multipartFile.getInputStream());
        FileDownloaded expectedFileDownloaded = new FileDownloaded(inputStreamResource,"application/txt","mockfile.txt");
        FileDownloaded responseFileDownloaded = bucketService.downloadFileFromBucket(bucketName,key);

        verify(s3Client).getObject(any(GetObjectRequest.class));
        assertThat(expectedFileDownloaded.getFileName()).isEqualTo(responseFileDownloaded.getFileName());
        assertThat(expectedFileDownloaded.getContentType()).isEqualTo(responseFileDownloaded.getContentType());
    }
    @Test
    void deleteFileFromBucketMatchKey(){
        String bucketname = "bucketname";
        String key = "key";
        S3Object s3Object1 = S3Object.builder().key(key).build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        bucketService.deleteFileFromBucket(bucketname,key);

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
    @Test
    void deleteFileFromBucketInvalidKey(){
        String bucketname = "bucketname";
        String key = "key";
        S3Object s3Object1 = S3Object.builder().key("difKey").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        assertThatThrownBy(()-> bucketService.deleteFileFromBucket(bucketname,key)).isInstanceOf(FileDeleteException.class);
        verify(s3Client,never()).deleteObject(any(DeleteObjectRequest.class));
    }
    @Test
    void deleteFileFromBucketThrowingS3Exception(){
        String bucketname = "bucketname";
        String key = "key";
        S3Object s3Object1 = S3Object.builder().key("key").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.deleteFileFromBucket(bucketname,key)).isInstanceOf(FileDeleteException.class);
        verify(snsService,never()).notifyFileDeleted(anyString());
    }
    @Test
    void moveFileToAnotherBucketMatchKey(){
        String sourceBucket = "sourcebucket";
        String targetBucket = "targetBucket";
        String key = "key";
        S3Object s3Object1 = S3Object.builder().key("key").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .build();
        DeleteObjectResponse deleteObjectResponse = DeleteObjectResponse.builder().build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(deleteObjectResponse);

        bucketService.moveFileToAnotherBucket(sourceBucket,targetBucket,key);

        verify(s3Client).copyObject(any(CopyObjectRequest.class));
    }
    @Test
    void moveFileToAnotherBucketInvalidKey(){
        String sourceBucket = "sourcebucket";
        String targetBucket = "targetBucket";
        String key = "key";
        S3Object s3Object1 = S3Object.builder().key("dif").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        assertThatThrownBy(()->bucketService.moveFileToAnotherBucket(sourceBucket,targetBucket,key))
                .isInstanceOf(FileDeleteException.class);

        verify(s3Client,never()).deleteObject(any(DeleteObjectRequest.class));
    }
    @Test
    void moveFileToAnotherBucketThrowingS3Exception(){
        String sourceBucket = "sourceBucket";
        String targetBucket = "targetBucket";
        String key = "key";
        when(s3Client.copyObject(any(CopyObjectRequest.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.moveFileToAnotherBucket(sourceBucket,targetBucket,key)).isInstanceOf(CopyFileException.class);
    }
    @Test
    void searchFileMultipleMatches(){
        String bucketName = "bucketname";
        String searchedString = "key";
        S3Object s3Object1 = S3Object.builder().key("key1").build();
        S3Object s3Object2 = S3Object.builder().key("key2").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        List<String> expectedFileKeys = List.of("key1","key2");
        boolean singleResult = false;
        boolean multipleResults = true;

        SearchFileResult expectedSearchFileResult = new SearchFileResult(expectedFileKeys,singleResult,multipleResults);
        SearchFileResult realSearchFileResult = bucketService.searchFile(bucketName,searchedString);

        assertThat(expectedSearchFileResult.getKey().size()).isEqualTo(realSearchFileResult.getKey().size());
        assertThat(expectedSearchFileResult.isExactFileNamePresent()).isEqualTo(realSearchFileResult.isExactFileNamePresent());
        assertThat(expectedSearchFileResult.isAnotherFilesWithTheCharSequence()).isEqualTo(realSearchFileResult.isAnotherFilesWithTheCharSequence());
    }
    @Test
    void searchFileNoMatches(){
        String bucketName = "bucketname";
        String searchedString = "g";
        S3Object s3Object1 = S3Object.builder().key("key1").build();
        S3Object s3Object2 = S3Object.builder().key("key2").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        SearchFileResult searchFileResult = bucketService.searchFile(bucketName,searchedString);
        assertThat(searchFileResult.getKey()).isNull();
        assertThat(searchFileResult.isExactFileNamePresent()).isFalse();
        assertThat(searchFileResult.isAnotherFilesWithTheCharSequence()).isFalse();
    }
    @Test
    void searchFileExactMatch(){
        String bucketName = "bucketname";
        String searchedString = "key1";
        S3Object s3Object1 = S3Object.builder().key("key1").build();
        S3Object s3Object2 = S3Object.builder().key("key2").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        List<String> expectedFileKeys = List.of("key1");
        boolean singleResult = true;
        boolean multipleResults = false;

        SearchFileResult expectedSearchFileResult = new SearchFileResult(expectedFileKeys,singleResult,multipleResults);
        SearchFileResult realSearchFileResult = bucketService.searchFile(bucketName,searchedString);

        assertThat(expectedSearchFileResult.getKey().size()).isEqualTo(realSearchFileResult.getKey().size());
        assertThat(expectedSearchFileResult.isExactFileNamePresent()).isEqualTo(realSearchFileResult.isExactFileNamePresent());
        assertThat(expectedSearchFileResult.isAnotherFilesWithTheCharSequence()).isEqualTo(realSearchFileResult.isAnotherFilesWithTheCharSequence());
    }
    @Test
    void searchFileThrowingS3Exception(){
        String bucketname = "bucketname";
        String searchedString = "searchedString";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.searchFile(bucketname,searchedString)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void listAllFolders(){
        String bucketName = "bucketname";
        S3Object s3Object1 = S3Object.builder().key("folder/anotherfolder/file.txt").build();
        S3Object s3Object2 = S3Object.builder().key("folder2/anotherfolder2/file2.pdf").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();
        Set<String> folders = new HashSet<>();
        folders.add("folder/anotherFolder");
        folders.add("folder2/anotherfolder2");

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        ListAllFoldersResult expectedListAllFoldersResult = new ListAllFoldersResult(folders,folders.size());
        ListAllFoldersResult realListAllFoldersResult = bucketService.listAllFolders(bucketName);
        assertThat(expectedListAllFoldersResult.getFolders().size()).isEqualTo(realListAllFoldersResult.getFolders().size());
        assertThat(expectedListAllFoldersResult.getNumberOfFolders()).isEqualTo(realListAllFoldersResult.getNumberOfFolders());
    }
    @Test
    void listAllFoldersEmptyResponse(){
        String bucketName = "bucketname";
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        ListAllFoldersResult listAllFoldersResult = bucketService.listAllFolders(bucketName);
        assertThat(listAllFoldersResult.getFolders()).isNull();
        assertThat(listAllFoldersResult.getNumberOfFolders()).isZero();
    }
    @Test
    void listAllFoldersThrowingS3Exception(){
        String bucketname = "bucketname";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.listAllFolders(bucketname)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void listAllFoldersSize(){
        String bucketName = "bucketname";
        S3Object s3Object1 = S3Object.builder().key("folder/anotherfolder/file.txt").size(10L).build();
        S3Object s3Object2 = S3Object.builder().key("folder2/anotherfolder2/file2.pdf").size(20L).build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        List<Map<String,String>> folders = List.of(
                Map.of("folder/anotherfolder","10"),
                Map.of("folder2/anotherfolder2","20")
        );

        FoldersSize expectedFoldersSize = new FoldersSize(folders,2,30.00);
        FoldersSize realFoldersSize = bucketService.listAllFoldersSize(bucketName);

        assertThat(expectedFoldersSize.getFolders().size()).isEqualTo(realFoldersSize.getFolders().size());
        assertThat(expectedFoldersSize.getTotalSize()).isEqualTo(realFoldersSize.getTotalSize());
        assertThat(expectedFoldersSize.getNumberOfFolders()).isEqualTo(realFoldersSize.getNumberOfFolders());
    }
    @Test
    void listAllFoldersSizeThrowingS3Exception(){
        String bucketname = "bucketname";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.listAllFoldersSize(bucketname)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void listAllFoldersSizeEmptyResponse(){
        String bucketname = "bucketname";
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder().build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        FoldersSize foldersSize = bucketService.listAllFoldersSize(bucketname);
        assertThat(foldersSize.getFolders().size()).isZero();
        assertThat(foldersSize.getTotalSize()).isEqualTo(0.00);
        assertThat(foldersSize.getNumberOfFolders()).isZero();

    }
    @Test
    void listAllFileExtensions(){
        String bucketName = "bucketname";
        S3Object s3Object1 = S3Object.builder().key("folder/anotherfolder/file.txt").build();
        S3Object s3Object2 = S3Object.builder().key("folder2/anotherfolder2/file2.pdf").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2)
                .build();

        Set<String> extensionsSet = Set.of("txt","pdf");

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        ListAllFileExtensions expectedListAllFileExtensions = new ListAllFileExtensions(extensionsSet,extensionsSet.size());
        ListAllFileExtensions realListAllFileExtensions = bucketService.listAllFileExtensions(bucketName);

        assertThat(expectedListAllFileExtensions.getFileExtensions().size()).isEqualTo(realListAllFileExtensions.getFileExtensions().size());
        assertThat(expectedListAllFileExtensions.getNumberOfExtensions()).isEqualTo(realListAllFileExtensions.getNumberOfExtensions());
    }
    @Test
    void listAllFileExtensionsEmptyResponse(){
        String bucketName = "bucketname";
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder().build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        ListAllFileExtensions realListAllFileExtensions = bucketService.listAllFileExtensions(bucketName);
        assertThat(realListAllFileExtensions.getFileExtensions()).isNull();
        assertThat(realListAllFileExtensions.getNumberOfExtensions()).isZero();
    }
    @Test
    void listAllFileExtensionsThrowingS3Exception(){
        String bucketname = "bucketname";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.listAllFileExtensions(bucketname)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void countExtensionOccurrences(){
        String bucketName = "bucketname";
        S3Object s3Object1 = S3Object.builder().key("folder/anotherfolder/file.txt").build();
        S3Object s3Object2 = S3Object.builder().key("folder2/anotherfolder2/file2.pdf").build();
        S3Object s3Object3 = S3Object.builder().key("folder2/anotherfolder2/file3.pdf").build();
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(s3Object1,s3Object2,s3Object3)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        List<String> expectedPdfFiles = List.of("folder2/anotherfolder2/file2.pdf", "folder2/anotherfolder2/file3.pdf");
        List<String> expectedTxtFile = List.of("folder/anotherfolder/file.txt");

        CountExtensionOccurrences expectedCountExtensionOccurrencesPdf = new CountExtensionOccurrences("pdf",2, expectedPdfFiles);
        CountExtensionOccurrences expectedCountExtensionOccurrencesTxt = new CountExtensionOccurrences("txt",1, expectedTxtFile);

        CountExtensionOccurrences realCountExtensionOccurrencesPdf = bucketService.countExtensionOccurrences(bucketName, "pdf");
        CountExtensionOccurrences realCountExtensionOccurrencesTxt = bucketService.countExtensionOccurrences(bucketName, "txt");

        assertThat(expectedCountExtensionOccurrencesPdf.getFileName().size()).isEqualTo(realCountExtensionOccurrencesPdf.getFileName().size());
        assertThat(expectedCountExtensionOccurrencesPdf.getExtension()).isEqualTo(realCountExtensionOccurrencesPdf.getExtension());
        assertThat(expectedCountExtensionOccurrencesPdf.getOccurrences()).isEqualTo(realCountExtensionOccurrencesPdf.getOccurrences());

        assertThat(expectedCountExtensionOccurrencesTxt.getFileName().size()).isEqualTo(realCountExtensionOccurrencesTxt.getFileName().size());
        assertThat(expectedCountExtensionOccurrencesTxt.getExtension()).isEqualTo(realCountExtensionOccurrencesTxt.getExtension());
        assertThat(expectedCountExtensionOccurrencesTxt.getOccurrences()).isEqualTo(realCountExtensionOccurrencesTxt.getOccurrences());
    }
    @Test
    void countExtensionOccurrencesThrowingS3Exception(){
        String bucketname = "bucketname";
        String extension = "pdf";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.countExtensionOccurrences(bucketname,extension)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void countExtensionOccurrencesEmptyResponse(){
        String bucketname = "bucketname";
        String extension = "pdf";
        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder().build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);
        CountExtensionOccurrences countExtensionOccurrences = bucketService.countExtensionOccurrences(bucketname,extension);
        assertThat(countExtensionOccurrences.getExtension()).isNull();
        assertThat(countExtensionOccurrences.getOccurrences()).isZero();
        assertThat(countExtensionOccurrences.getFileName()).isNull();
    }
    @Test
    void listAllBucketContent() {
        String bucketName = "bucket-1";
        List<S3Object> objectList = List.of(
            createS3Object("key1","etag1"),
            createS3Object("key2","etag2"),
            createS3Object("key3","etag3")
        );

        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(objectList)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        BucketContent bucketContent = bucketService.listAllBucketContent(bucketName);

        List<Content> expectedContentList = objectList
                .stream()
                .map(Content::new)
                .collect(Collectors.toList());

        BucketContent expectedBucketContent = new BucketContent(expectedContentList);

        assertThat(bucketContent.getObjectList()).isNotNull();
        assertThat(expectedBucketContent.getObjectList()).isNotNull();
        assertThat(expectedBucketContent.getObjectList().size()).isEqualTo(bucketContent.getObjectList().size());

    }
    @Test
    void listAllBucketContentEmptyResponse(){
        String bucketname = "bucketname";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(null);
        BucketContent bucketContent = bucketService.listAllBucketContent(bucketname);
        assertThat(bucketContent.getObjectList().size()).isZero();
    }
    @Test
    void listAllBucketContentThrowingS3Exception(){
        String bucketname = "bucketname";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.listAllBucketContent(bucketname)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getBucketContentDetailsByKey() {
        String bucketName = "bucket-name";
        S3Object s3Object1 = createS3Object("key1", "etag1");
        S3Object s3Object2 = createS3Object("key2", "etag2");
        S3Object s3Object3 = createS3Object("key3", "etag3");
        List<S3Object> objectList = List.of(
                s3Object1,
                s3Object2,
                s3Object3
        );

        ListObjectsV2Response listObjectsV2Response = ListObjectsV2Response.builder()
                .contents(objectList)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listObjectsV2Response);

        ContentDetails contentDetails1 = bucketService.getBucketContentDetailsByKey(bucketName,"key1");
        ContentDetails contentDetails2 = bucketService.getBucketContentDetailsByKey(bucketName,"key2");
        ContentDetails contentDetails3 = bucketService.getBucketContentDetailsByKey(bucketName,"key3");

        assertThat(contentDetails1.geteTag()).isEqualTo(s3Object1.eTag());
        assertThat(contentDetails2.geteTag()).isEqualTo(s3Object2.eTag());
        assertThat(contentDetails3.geteTag()).isEqualTo(s3Object3.eTag());
    }
    @Test
    void getBucketContentDetailsByKeyThrowingS3Exception(){
        String bucketname = "bucketname";
        String key = "key";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.getBucketContentDetailsByKey(bucketname,key)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getBucketContentDetailsByKeyEmptyResponse(){
        String bucketname = "bucketname";
        String key = "key";
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(null);
        ContentDetails bucketContent = bucketService.getBucketContentDetailsByKey(bucketname,key);
        assertThat(bucketContent.getOwner()).isNull();
    }
    @Test
    void uploadFileToBucket_WhenFileDoesNotExist_UploadsFileAndNotifies() {
        String bucketName = "bucket-1";
        String fileName = "test.txt";
        String contentType = "text/plain";

        byte[] fileContent = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, contentType, fileContent);

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class);

        bucketService.updateFileToBucket(file, bucketName);

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(snsService).notifyFileUploaded("File uploaded to bucket : " + bucketName);
    }
    @Test
    void uploadFileToBucketThrowingS3Exception(){
        String bucketName = "bucket-1";
        String fileName = "test.txt";
        String contentType = "text/plain";

        byte[] fileContent = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, contentType, fileContent);

        when(s3Client.headObject(any(HeadObjectRequest.class))).thenThrow(NoSuchKeyException.class);
        when(s3Client.putObject(any(PutObjectRequest.class),any(RequestBody.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.updateFileToBucket(file,bucketName)).isInstanceOf(FileUploadException.class);
    }

    @Test
    void generateFileUrl() throws MalformedURLException {
        String bucketName = "bucket-sample";
        String key = "/file/samplefile.json";
        String expirationTime = "60"; // minutes
        String mockUrl = "https://aws.amazonaws.com/";

        PresignedGetObjectRequest presignedGetObjectRequestMock = mock(PresignedGetObjectRequest.class);
        URL urlMock = new URL(mockUrl);
        when(presignedGetObjectRequestMock.url()).thenReturn(urlMock);
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGetObjectRequestMock);

        String presignUrl = bucketService.generateFileUrl(bucketName, key, expirationTime);

        assertThat(presignUrl).isEqualTo(mockUrl);
    }
    @Test
    void generateFileUrlThrowingS3Exception(){
        String bucketname = "bucketname";
        String key = "key";
        String expirationTime ="1000";
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(S3Exception.builder().build());
        assertThatThrownBy(()->bucketService.generateFileUrl(bucketname,key,expirationTime)).isInstanceOf(PresignUrlException.class);
    }
    @Test
    void uploadFileToBucket_WhenFileExists_ThrowsFileAlreadyExistsException() {
        String bucketName = "bucket-1";
        String fileName = "test.txt";
        String contentType = "text/plain";

        byte[] fileContent = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(fileName, fileName, contentType, fileContent);

        HeadObjectResponse headObjectResponse = HeadObjectResponse.builder().build();
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(headObjectResponse);

        assertThrows(FileAlreadyExistsException.class, () -> bucketService.updateFileToBucket(file, bucketName));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(snsService, never()).notifyFileUploaded(anyString());
    }

    private static S3Object createS3Object(String key,String eTag){
        return S3Object
                .builder()
                .eTag(eTag)
                .key(key)
                .lastModified(Instant.now())
                .size(0L)
                .storageClass(ObjectStorageClass.STANDARD)
                .owner(Owner.builder().id("owner-id").displayName("owner-name").build())
                .build();
    }
}