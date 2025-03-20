package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SchedulerJobWsDto;
import com.touchMind.core.mongo.model.SchedulerJob;

import java.util.List;

public interface SchedulerJobService {
    SchedulerJob findCronJobById(String objectId);

    SchedulerJob findByIdentifier(String recordId);

    void deleteCronJobById(String id);

    List<SchedulerJob> findAll();

    SchedulerJob save(SchedulerJob cronJob);

    void startCronJob(String id);

    void stopCronJob(String id);

    SchedulerJobWsDto handleEdit(SchedulerJobWsDto request);
}
