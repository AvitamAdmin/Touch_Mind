package com.cheil.core.service.cronjob;

import com.cheil.core.mongo.dto.CronJobWsDto;
import com.cheil.core.mongo.model.CronJob;
import com.cheil.core.service.impl.CronService;
import com.cheil.form.CronForm;
import com.cheil.qa.service.impl.CronDefinition;

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
