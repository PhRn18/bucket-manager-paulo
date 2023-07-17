package com.project.bucketmanager.Services.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SnsServiceImplTest {
    @Mock
    private SnsClient snsClient;
    private SnsServiceImpl snsServiceNotificationsEnabled;
    private SnsServiceImpl snsServiceNotificationsDisabled;
    private ObjectMapper objectMapper;
    private final String snsFileDeletedArn = "sns-file-delete-arn";
    private final String snsFileUploadedArn = "sns-file-uploaded-arn";
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        snsServiceNotificationsEnabled = new SnsServiceImpl(snsFileDeletedArn,snsFileUploadedArn,snsClient,objectMapper,true);
        snsServiceNotificationsDisabled = new SnsServiceImpl(snsFileDeletedArn,snsFileUploadedArn,snsClient,objectMapper,false);
    }
    @Test
    void testNotifyFileUploadedNotificationsEnabled() throws JsonProcessingException {
        String messageParsed = "file-uploaded";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsServiceNotificationsEnabled.notifyFileUploaded(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileUploadedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient).publish(build);
    }
    @Test
    void testNotifyFileUploadedNotificationsDisabled() throws JsonProcessingException {
        String messageParsed = "file-uploaded";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsServiceNotificationsDisabled.notifyFileUploaded(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileUploadedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient,never()).publish(build);
    }
    @Test
    void testNotifyFileDeletedNotificationsEnabled() throws JsonProcessingException {
        String messageParsed = "file-deleted";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsServiceNotificationsEnabled.notifyFileDeleted(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileDeletedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient).publish(build);
    }
    @Test
    void testNotifyFileDeletedNotificationsDisabled() throws JsonProcessingException {
        String messageParsed = "file-deleted";
        String expectedMessage = objectMapper.writeValueAsString(messageParsed);
        PublishResponse publishResponse = PublishResponse
                .builder()
                .build();

        when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

        snsServiceNotificationsDisabled.notifyFileDeleted(messageParsed);

        PublishRequest build = PublishRequest.builder()
                .topicArn(snsFileDeletedArn)
                .message(expectedMessage)
                .build();

        verify(snsClient,never()).publish(build);
    }
    @Test
    void testPublishMessageThrowingSnsException()  {
        snsServiceNotificationsEnabled.publishMessage("anytopicarn","");
        when(snsClient.publish(any(PublishRequest.class))).thenThrow(SnsException.builder().build());
        assertThatThrownBy(()->snsServiceNotificationsEnabled.publishMessage("anytopicarn","message")).isInstanceOf(RuntimeException.class);
    }
}