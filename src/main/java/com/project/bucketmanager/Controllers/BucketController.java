package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Config.AutoCreateBuckets;
import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Models.enums.EContentDisposition;
import com.project.bucketmanager.Services.BucketService;
import com.project.bucketmanager.Aspects.Validation.Security.AllowReadAndWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/bucket")
public class BucketController {
    private static final Logger logger = LoggerFactory.getLogger(AutoCreateBuckets.class);
    private final BucketService bucketService;
    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }
    @GetMapping("/list")
    public ResponseEntity<List<BucketDetails>> listAllBuckets(){
        logger.info("[BucketController]-Listing all buckets...");
        List<BucketDetails> result =  bucketService.listAllBuckets();
        if(result.isEmpty()){
            return ResponseEntity
                    .noContent()
                    .build();
        }
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<BucketContent> listAllBucketContent(@PathVariable String bucketName){
        logger.info("[BucketController]-Listing all bucket content...");
        BucketContent result = bucketService.listAllBucketContent(bucketName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<ContentDetails> listBucketContentDetails(
            @PathVariable String bucketName,
            @RequestParam String key
    ){
        logger.info("[BucketController]-Listing bucket content details...");
        ContentDetails contentDetails = bucketService.getBucketContentDetailsByKey(bucketName,key);
        return ResponseEntity.ok(contentDetails);
    }

    @PostMapping("/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<FileUploaded> uploadFileToBucket(
            @RequestParam("file")MultipartFile file,
            @PathVariable String bucketName
    ){
        logger.info("[BucketController]-Uploading file to bucket...");
        bucketService.updateFileToBucket(file,bucketName);
        FileUploaded fileUploaded = new FileUploaded("File uploaded!",file.getSize(),file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploaded);
    }

    @PostMapping("/compress/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<CompressedFileUpdate> compressedFileUpdateToBucket(
            @RequestParam("file") MultipartFile file,
            @PathVariable String bucketName
    ){
        logger.info("[BucketController]-Compressing and uploading file to bucket...");
        CompressedFileUpdate result = bucketService.compressAndUpdateFileToBucket(file, bucketName);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/download/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<Resource> downloadFileFromBucket(
            @PathVariable String bucketName,
            @RequestParam String key,
            @RequestParam(required = false) String contentDisposition
    ){
        logger.info("[BucketController]-Downloading file from bucket...");
        EContentDisposition contentDispositionValue = EContentDisposition.getByValue(contentDisposition);
        FileDownloaded fileDownloaded = bucketService.downloadFileFromBucket(bucketName, key);
        String contentType = fileDownloaded.getContentType();
        InputStreamResource inputStreamResource = fileDownloaded.getInputStreamResource();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder(contentDispositionValue.getValue()).filename(fileDownloaded.getFileName()).build());
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    @GetMapping("/url/{bucketName}/{expirationTime}")
    @AllowReadAndWrite
    public ResponseEntity<String> generateFileUrl(
            @PathVariable String bucketName,
            @RequestParam String key,
            @PathVariable String expirationTime
    ){
        logger.info("[BucketController]-Generating file url...");
        String url = bucketService.generateFileUrl(bucketName,key,expirationTime);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/search/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<SearchFileResult> searchFile(
            @PathVariable String bucketName,
            @RequestParam String searchString
    ){
        logger.info("[BucketController]-Searching file...");
        SearchFileResult result = bucketService.searchFile(bucketName, searchString);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/folders/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<ListAllFoldersResult> listAllFolders(
            @PathVariable String bucketName
    ){
        logger.info("[BucketController]-Listing all folders...");
        ListAllFoldersResult result = bucketService.listAllFolders(bucketName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/folders/listSize/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<FoldersSize> listAllFoldersSize(
            @PathVariable String bucketName
    ){
        logger.info("[BucketController]-Listing all folders size...");
        FoldersSize foldersSize = bucketService.listAllFoldersSize(bucketName);

        return ResponseEntity.ok(foldersSize);
    }

    @GetMapping("/fileExtensions/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<ListAllFileExtensions> listAllFileExtensions(
            @PathVariable String bucketName
    ){
        logger.info("[BucketController]-Listing all file extensions...");
        ListAllFileExtensions result = bucketService.listAllFileExtensions(bucketName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fileExtensions/count/{bucketName}/{extension}")
    @AllowReadAndWrite
    public ResponseEntity<CountExtensionOccurrences> countExtensionOccurrences(
            @PathVariable String bucketName,
            @PathVariable String extension
    ){
        logger.info("[BucketController]-Couting extension occurrences...");
        CountExtensionOccurrences result = bucketService.countExtensionOccurrences(bucketName,extension);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/move/{sourceBucket}/{targetBucket}")
    @AllowReadAndWrite
    public ResponseEntity<?> moveFileToAnotherBucket(
            @PathVariable String sourceBucket,
            @PathVariable String targetBucket,
            @RequestParam String key
    ){
        logger.info("[BucketController]-Moving file to another bucket...");
        bucketService.moveFileToAnotherBucket(sourceBucket,targetBucket,key);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{bucketName}")
    @AllowReadAndWrite
    public ResponseEntity<?> deleteBucketFile(
            @PathVariable String bucketName,
            @RequestParam String key
    ){
        logger.warn("[BucketController]-Deleting file...");
        bucketService.deleteFileFromBucket(bucketName, key);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
