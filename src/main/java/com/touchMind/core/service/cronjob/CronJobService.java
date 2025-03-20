package com.touchMind.core.service.cronjob;

import com.touchMind.core.mongo.dto.CronJobWsDto;
import com.touchMind.core.mongo.model.CronJob;
import com.touchMind.core.service.impl.CronService;
import com.touchMind.form.CronForm;
import com.touchMind.qa.service.impl.CronDefinition;

import java.util.List;
import java.util.Map;

public interface CronJobService {
    void stopCronJob(String id);

    CronJob findCronJobById(String objectId);

    CronJob save(CronForm cronForm);

    void deleteCronJobById(String id);

    List<CronJob> findAll();

    CronDefinition getTaskDefinition(Map<String, String> data, String id);

    CronJob save(CronJob cronJob);

    void startCronJob(String id, CronService cronService);

    CronJobWsDto handleEdit(CronJobWsDto request);
}
