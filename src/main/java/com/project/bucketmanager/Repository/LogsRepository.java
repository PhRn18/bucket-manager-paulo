package com.project.bucketmanager.Repository;

import com.project.bucketmanager.Models.Log;

public interface LogsRepository {
    void create(Log log);
    Log listAllLogs();
}
