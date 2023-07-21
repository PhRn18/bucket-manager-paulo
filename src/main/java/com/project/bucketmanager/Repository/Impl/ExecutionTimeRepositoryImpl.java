package com.project.bucketmanager.Repository.Impl;

import com.project.bucketmanager.Models.AverageExecutionTime;
import com.project.bucketmanager.Models.ExecutionTime;
import com.project.bucketmanager.Repository.ExecutionTimeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ExecutionTimeRepositoryImpl implements ExecutionTimeRepository {
    private final JdbcTemplate jdbcTemplate;
    public ExecutionTimeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    @Transactional
    public void create(ExecutionTime executionTime) {
        String sql = """
            INSERT INTO execution_time (method_name, execution_time) VALUES (?, ?)
        """.trim();
        jdbcTemplate.update(
                sql,
                executionTime.getMethodName(),
                executionTime.getExecutionTime()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExecutionTime> listAll() {
        String sql = """
            SELECT * FROM execution_time ORDER BY id      
        """;
        return jdbcTemplate.query(sql, (rs, l) -> {
            ExecutionTime executionTime = new ExecutionTime();
            executionTime.setExecutionTime(Long.parseLong(rs.getString("execution_time")));
            executionTime.setMethodName(rs.getString("method_name"));
            return executionTime;
        });
    }

    @Override
    public ExecutionTime getTheLongestRecord() {
        String sql = """
                SELECT * FROM execution_time ORDER BY execution_time DESC limit 1;
        """;
        return jdbcTemplate.query(sql, (rs) -> {
            ExecutionTime executionTime = new ExecutionTime();
            executionTime.setExecutionTime(Long.parseLong(rs.getString("execution_time")));
            executionTime.setMethodName(rs.getString("method_name"));
            return executionTime;
        });
    }

    @Override
    public AverageExecutionTime getTheAverageExecutionTime() {
        String sql = """
                    select avg(execution_time) as averageExecutionTime
                            from execution_time
                            limit 20;
        """;
        return jdbcTemplate.query(sql,(rs)->{
            long time = Long.parseLong(rs.getString("averageExecutionTime"));
            return new AverageExecutionTime(time);
        });
    }
}
