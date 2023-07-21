package com.project.bucketmanager.Repository;

import com.project.bucketmanager.Models.AverageExecutionTime;
import com.project.bucketmanager.Models.ExecutionTime;

import java.util.List;

public interface    ExecutionTimeRepository {
    void create(ExecutionTime executionTime);
    List<ExecutionTime> listAll();
    ExecutionTime getTheLongestRecord();
    AverageExecutionTime getTheAverageExecutionTime();
}
