package com.project.bucketmanager.Services.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bucketmanager.Services.SnsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsServiceImpl implements SnsService {
    private final String snsFileDeletedArn;
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final boolean enableNotification;
    public SnsServiceImpl(
            @Value("${aws.sns.file.deleted.arn}") String snsFileDeletedArn,
            SnsClient snsClient,
            ObjectMapper objectMapper,
            @Value("${aws.enable.notification}") boolean enableNotification) {
        this.snsFileDeletedArn = snsFileDeletedArn;
        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
        this.enableNotification = enableNotification;
    }
    @Override
    public void notifyFileDeleted(Object message) {
        publishMessage(snsFileDeletedArn,message);
    }

    private void publishMessage(String topicArn, Object message) {
        if(enableNotification){
            try{
                String messageParsed = objectMapper.writeValueAsString(message);
                PublishRequest publishRequest = PublishRequest.builder()
                        .topicArn(topicArn)
                        .message(messageParsed)
                        .build();
                snsClient.publish(publishRequest);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
