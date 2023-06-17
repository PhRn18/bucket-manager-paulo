package com.project.bucketmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BucketManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BucketManagerApplication.class, args);
    }

}
