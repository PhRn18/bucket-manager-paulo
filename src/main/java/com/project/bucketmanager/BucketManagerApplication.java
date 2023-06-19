package com.project.bucketmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableCaching
@EnableSwagger2
public class BucketManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BucketManagerApplication.class, args);
    }
    public Docket apis(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.bucketmanager.Controllers"))
                .build();
    }

}
