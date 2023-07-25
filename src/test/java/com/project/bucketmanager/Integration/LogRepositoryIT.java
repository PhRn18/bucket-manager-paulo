package com.project.bucketmanager.Integration;

import com.project.bucketmanager.Models.Log;
import com.project.bucketmanager.Repository.LogsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
@SpringBootTest
@Testcontainers
public class LogRepositoryIT {
    @Autowired
    private LogsRepository logsRepository;

    private final List<Log> mockLogs = List.of(
            new Log("bucket1","user1","operation1","time1","ex1"),
            new Log("bucket2","user2","operation2","time2","ex2"));

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:13-alpine")
    ).withDatabaseName("bucketmanager")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.datasource.url",postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.password",postgreSQLContainer::getPassword);
        dynamicPropertyRegistry.add("spring.datasource.username",postgreSQLContainer::getUsername);
    }

    @Test
    void containerUp(){
        boolean running = postgreSQLContainer.isRunning();
        assertThat(running).isTrue();
    }

    @Test
    @Rollback(value = false)
    void createAndList(){
        mockLogs.forEach(logsRepository::create);
        List<Log> logs = logsRepository.listAllLogs();
        assertThat(logs.size()).isEqualTo(mockLogs.size());
    }

}
