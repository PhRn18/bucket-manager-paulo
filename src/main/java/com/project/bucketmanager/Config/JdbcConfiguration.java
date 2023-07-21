package com.project.bucketmanager.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {
    private final DataSource dataSource;
    public JdbcConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }
}
