package com.project.bucketmanager.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class AutoCreateBuckets {
    private static final Logger logger = LoggerFactory.getLogger(AutoCreateBuckets.class);
    private final Environment environment;
    private final S3Client s3Client;
    private final boolean autoCreateBuckets;
    public AutoCreateBuckets(Environment environment, S3Client s3Client,@Value("${aws.enable.autoCreateBuckets}")boolean autoCreateBuckets) {
        this.environment = environment;
        this.s3Client = s3Client;
        this.autoCreateBuckets = autoCreateBuckets;
    }

    @PostConstruct
    public void createBuckets(){
        if(!autoCreateBuckets){
            return;
        }
        String[] names = environment.getRequiredProperty("aws.bucket.names", String[].class);

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        List<Bucket> buckets = s3Client.listBuckets(listBucketsRequest).buckets();

        for (String name : names) {
            boolean bucketAlreadyExists = !buckets.isEmpty() && buckets.stream().anyMatch(bucket -> bucket.name().equals(name));
            if (bucketAlreadyExists) {
                logger.warn("Bucket already exists: " + name);
            } else {

                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(name)
                        .build();

                CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);
                logger.info("Bucket created: " + createBucketResponse.location());
            }
        }
    }
}
