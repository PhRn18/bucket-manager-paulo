package com.project.bucketmanager.Services;


public interface SnsService {
    void notifyFileDeleted(Object message);
    void notifyFileUploaded(Object message);
}
