package com.project.bucketmanager.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

import java.net.URI;

@Configuration
public class AWSConfig {
    private Logger logger = LoggerFactory.getLogger(AWSConfig.class);

    @Value("${aws.bucket.region}")
    private String bucketRegion;

    @Value("${aws.sns.region}")
    private String snsRegion;

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public S3Presigner s3PresignerDev(@Value("${aws.bucket.endpoint}") String bucketEndpoint) {
        return S3Presigner.builder()
                .region(Region.of(bucketRegion))
                .endpointOverride(URI.create(bucketEndpoint))
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod", matchIfMissing = true)
    public S3Presigner s3PresignerProd() {
        return S3Presigner.builder()
                .region(Region.of(bucketRegion))
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public S3Client buildDevS3Client(@Value("${aws.bucket.endpoint}") String bucketEndpoint) {
        logger.info("Building [DEV] S3 Client");
        return getBuilder().endpointOverride(URI.create(bucketEndpoint)).build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod", matchIfMissing = true)
    public S3Client buildProdS3Client() {
        logger.info("Building [PROD] S3 Client");
        return getBuilder().build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public SnsClient buildDevSnsClient(@Value("${aws.sns.endpoint}") String snsEndpoint){
        return getSnsBuilder().endpointOverride(URI.create(snsEndpoint)).build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod", matchIfMissing = true)
    public SnsClient buildProdSnsClient(){
        return getSnsBuilder().build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public CloudWatchClient buildDevCloudWatchClient(@Value("${aws.bucket.endpoint}") String endpoint){
        return getCloudWatchClient().endpointOverride(URI.create(endpoint)).build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod", matchIfMissing = true)
    public CloudWatchClient buildProdCloudWatchClient(){
        return getCloudWatchClient().build();
    }
    private CloudWatchClientBuilder getCloudWatchClient(){
        return CloudWatchClient.builder()
                .region(Region.of(bucketRegion));
    }
    private SnsClientBuilder getSnsBuilder(){
        return SnsClient.builder()
                .region(Region.of(snsRegion));
    }
    private S3ClientBuilder getBuilder() {
        return S3Client.builder()
                .region(Region.of(bucketRegion));
    }
}
