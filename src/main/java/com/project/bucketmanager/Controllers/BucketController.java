package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.ExceptionHandler.Exceptions.EmptyFileException;
import com.project.bucketmanager.Models.BucketContent;
import com.project.bucketmanager.Models.ContentDetails;
import com.project.bucketmanager.Models.FileDownloaded;
import com.project.bucketmanager.Models.FileUploaded;
import com.project.bucketmanager.Services.BucketService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bucket")
public class BucketController {
    private final BucketService bucketService;
    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping("/{bucketName}")
    public ResponseEntity<BucketContent> listAllBucketContent(@PathVariable String bucketName){
        BucketContent result = bucketService.listAllBucketContent(bucketName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details/{bucketName}/{key}")
    public ResponseEntity<ContentDetails> listBucketContentDetails(
            @PathVariable String bucketName,
            @PathVariable String key
    ){
        ContentDetails contentDetails = bucketService.getBucketContentDetailsByKey(bucketName,key);
        return ResponseEntity.ok(contentDetails);
    }

    @PostMapping("/{bucketName}")
    public ResponseEntity<FileUploaded> uploadFileToBucket(
            @RequestParam("file")MultipartFile file,
            @PathVariable String bucketName
    ){
        bucketService.updateFileToBucket(file,bucketName);
        FileUploaded fileUploaded = new FileUploaded("File uploaded!",file.getSize(),file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploaded);
    }

    @GetMapping("/download/{bucketName}/{key}/{contentDisposition}")
    public ResponseEntity<Resource> downloadFileFromBucket(
            @PathVariable String bucketName,
            @PathVariable String key,
            @PathVariable(required = false) String contentDisposition
    ){
        String contentDispositionValue = "attachment";
        if (contentDisposition != null && !contentDisposition.isEmpty()) {
            if (!contentDisposition.equals("inline") && !contentDisposition.equals("attachment")) {
                throw new IllegalArgumentException("Invalid content disposition!");
            }
            contentDispositionValue = contentDisposition;
        }
        FileDownloaded fileDownloaded = bucketService.downloadFileFromBucket(bucketName, key);
        String contentType = fileDownloaded.getContentType();
        InputStreamResource inputStreamResource = fileDownloaded.getInputStreamResource();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder(contentDispositionValue).filename(key).build());
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{bucketName}/{key}")
    public ResponseEntity<?> deleteBucketFile(
            @PathVariable String bucketName,
            @PathVariable String key
    ){
        bucketService.deleteFileFromBucket(bucketName, key);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
