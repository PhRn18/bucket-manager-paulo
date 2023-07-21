package com.project.bucketmanager.Repository;

import com.project.bucketmanager.Models.Log;

import java.util.List;

public interface LogsRepository {
    void create(Log log);
    List<Log> listAllLogs();
}
