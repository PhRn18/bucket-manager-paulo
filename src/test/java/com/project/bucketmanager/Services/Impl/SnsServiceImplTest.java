package com.project.bucketmanager.Services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SnsServiceImplTest {
    @Mock
    private SnsClient snsClient;
    private SnsServiceImpl snsService;
    private ObjectMapper objectMapper;
    private final String snsFileDeletedArn = "sns-file-delete-arn";
    private final String snsFileUploadedArn = "sns-file-uploaded-arn";
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        snsService = new SnsServiceImpl(snsFileDeletedArn,snsFileUploadedArn,snsClient,objectMapper,true);
    }
    @Test
    void testNotifyFileUploaded() throws JsonProcessingException {
        String messageParsed = "file-uploaded";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsService.notifyFileUploaded(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileUploadedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient).publish(build);
    }
    @Test
    void testNotifyFileDeleted() throws JsonProcessingException {
        String messageParsed = "file-deleted";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsService.notifyFileDeleted(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileDeletedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient).publish(build);
    }
}