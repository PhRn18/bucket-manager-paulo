package com.project.bucketmanager.Services;

import software.amazon.awssdk.services.sns.model.PublishRequest;

public interface SnsService {
    public void notifyFileDeleted(Object message);
}
