package com.project.bucketmanager.Controllers;

import com.project.bucketmanager.Models.*;
import com.project.bucketmanager.Services.BucketService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/bucket")
public class BucketController {
    private final BucketService bucketService;
    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<BucketDetails>> listAllBuckets(){
        List<BucketDetails> result =  bucketService.listAllBuckets();
        if(result.isEmpty()){
            return ResponseEntity
                    .noContent()
                    .build();
        }
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{bucketName}")
    public ResponseEntity<BucketContent> listAllBucketContent(@PathVariable String bucketName){
        BucketContent result = bucketService.listAllBucketContent(bucketName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details/{bucketName}")
    public ResponseEntity<ContentDetails> listBucketContentDetails(
            @PathVariable String bucketName,
            @RequestParam String key
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

    @GetMapping("/download/{bucketName}/{contentDisposition}")
    public ResponseEntity<Resource> downloadFileFromBucket(
            @PathVariable String bucketName,
            @RequestParam String key,
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

    @GetMapping("/url/{bucketName}/{expirationTime}")
    public ResponseEntity<String> generateFileUrl(
            @PathVariable String bucketName,
            @RequestParam String key,
            @PathVariable String expirationTime
    ){
        String url = bucketService.generateFileUrl(bucketName,key,expirationTime);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/search/{bucketName}")
    public ResponseEntity<SearchFileResult> searchFile(
            @PathVariable String bucketName,
            @RequestParam String searchString
    ){
        SearchFileResult result = bucketService.searchFile(bucketName, searchString);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/folders/{bucketName}")
    public ResponseEntity<ListAllFoldersResult> listAllFolders(
            @PathVariable String bucketName
    ){
        ListAllFoldersResult result = bucketService.listAllFolders(bucketName);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{bucketName}")
    public ResponseEntity<?> deleteBucketFile(
            @PathVariable String bucketName,
            @RequestParam String key
    ){
        bucketService.deleteFileFromBucket(bucketName, key);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
