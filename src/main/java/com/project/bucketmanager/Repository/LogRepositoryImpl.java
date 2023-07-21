package com.project.bucketmanager.Repository;

import com.project.bucketmanager.Models.Log;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LogRepositoryImpl implements LogsRepository{
    private final JdbcTemplate jdbcTemplate;
    public LogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void create(Log log) {
        String sql = """
            INSERT INTO logs (bucket, username, operation, time, exception) VALUES (?, ?, ?, ?, ?)
        """.trim();
        jdbcTemplate.update(sql, log.getBucket(), log.getUser(), log.getOperation(), log.getTime(),log.getException());
    }

    @Override
    public Log listAllLogs() {
        return null;
    }
}
