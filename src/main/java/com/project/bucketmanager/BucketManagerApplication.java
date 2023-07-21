package com.project.bucketmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableCaching
@EnableSwagger2
@EnableScheduling
@EnableAspectJAutoProxy
public class BucketManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BucketManagerApplication.class, args);
    }

}
