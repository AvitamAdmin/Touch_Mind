package com.touchmind.core.service.cronjob;

import com.touchmind.core.mongo.dto.CronJobDto;
import com.touchmind.core.mongo.dto.CronJobWsDto;
import com.touchmind.core.mongo.model.CronJob;
import com.touchmind.core.service.impl.CronService;
import com.touchmind.qa.service.impl.CronDefinition;

import java.util.List;
import java.util.Map;

public interface CronJobService {
    void stopCronJob(String id);

    CronJob findCronJobById(String objectId);

    CronJob save(CronJobDto cronForm);

    void deleteCronJobById(String id);

    List<CronJob> findAll();

    CronDefinition getTaskDefinition(Map<String, String> data, String id);

    CronJob save(CronJob cronJob);

    void startCronJob(String id, CronService cronService);

    CronJobWsDto handleEdit(CronJobWsDto request);
}
