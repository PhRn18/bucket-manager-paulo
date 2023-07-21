package com.project.bucketmanager.Repository.Impl;

import com.project.bucketmanager.Models.Log;
import com.project.bucketmanager.Repository.LogsRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LogRepositoryImpl implements LogsRepository {
    private final JdbcTemplate jdbcTemplate;
    public LogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    @Transactional
    public void create(Log log) {
        String sql = """
            INSERT INTO logs (bucket, username, operation, time, exception) VALUES (?, ?, ?, ?, ?)
        """.trim();
        jdbcTemplate.update(sql, log.getBucket(), log.getUser(), log.getOperation(), log.getTime(),log.getException());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> listAllLogs() {
        String sql = """
            SELECT * FROM logs ORDER BY id
        """.trim();

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Log log = new Log();
            log.setUser(rs.getString("username"));
            log.setTime(rs.getString("time"));
            log.setException(rs.getString("exception"));
            log.setOperation(rs.getString("operation"));
            log.setBucket(rs.getString("bucket"));
            return log;
        });
    }
}
