package com.project.bucketmanager.Jobs;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefreshBucketListCache {

    @Scheduled(cron = "0 */20 * ? * *")
    @CacheEvict(value = "cachedBucketList", allEntries = true)
    public void refreshCache(){}


}
