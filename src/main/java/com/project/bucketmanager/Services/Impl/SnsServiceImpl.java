package com.project.bucketmanager.Services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Services.SnsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;


@Service
public class SnsServiceImpl implements SnsService {
    private static final Logger logger = LoggerFactory.getLogger(SnsServiceImpl.class);
    private final String snsFileDeletedArn;
    public final String snsFileUploadedArn;
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final boolean enableNotification;
    public SnsServiceImpl(
            @Value("${aws.sns.file.deleted.arn}") String snsFileDeletedArn,
            @Value("${aws.sns.file.uploaded.arn}") String snsFileUploadedArn,
            SnsClient snsClient,
            ObjectMapper objectMapper,
            @Value("${aws.enable.notification}") boolean enableNotification) {
        this.snsFileDeletedArn = snsFileDeletedArn;
        this.snsFileUploadedArn = snsFileUploadedArn;
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.enableNotification = enableNotification;
    }
    @Override
    public void notifyFileDeleted(Object message) {
        publishMessage(snsFileDeletedArn,message);
    }

    @Override
    public void notifyFileUploaded(Object message) {
        publishMessage(snsFileUploadedArn,message);
    }

    protected void publishMessage(String topicArn, Object message) {
        if(enableNotification){
            try{
                String messageParsed = objectMapper.writeValueAsString(message);
                PublishRequest publishRequest = PublishRequest.builder()
                        .topicArn(topicArn)
                        .message(messageParsed)
                        .build();
                snsClient.publish(publishRequest);
            }catch (SnsException | JsonProcessingException e){
                throw new RuntimeException(e.getMessage());
            }
        }else{
            logger.warn("Notifications are not enabled!");
        }
    }
}
