package com.project.bucketmanager.ExceptionHandler;

import com.project.bucketmanager.ExceptionHandler.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleContentNotFoundException(ContentNotFoundException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<Map<String,Object>> handleEmptyFileException(EmptyFileException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String,Object>> handleFileUploadException(FileUploadException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }
    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleFileAlreadyExistsException(FileAlreadyExistsException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<Map<String,Object>> handleFileDownloadException(FileDownloadException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<Map<String,Object>> handleFileDeleteException(FileDeleteException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String,Object> response = buildDefaultErrorMap(ex,status);
        return ResponseEntity.status(status).body(response);
    }

    protected Map<String,Object> buildDefaultErrorMap(Exception ex,HttpStatus status){
        Map<String,Object> response = new HashMap<>();
        response.put("HTTP_STATUS",status);
        response.put("TIMESTAMP", LocalDateTime.now());
        response.put("MESSAGE",ex.getMessage());
        return response;
    }

}