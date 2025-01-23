package com.touchmind.HealthcheckMvp;

import com.touchmind.core.mongo.model.CronJob;
import com.touchmind.core.service.cronjob.CronJobService;
import com.touchmind.qa.service.QualityAssuranceService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationCronJobs {

    public static final String TOOLKIT = "/toolkit/";
    @Autowired
    CronJobService cronJobService;
    @Autowired
    QualityAssuranceService qualityAssuranceService;
    Logger logger = LoggerFactory.getLogger(ApplicationCronJobs.class);
    @Autowired
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    public void scheduleJobs() {
        List<CronTask> tasks = scheduledTaskRegistrar.getCronTaskList();

        if (CollectionUtils.isEmpty(tasks)) {
            logger.info("==QA rescheduling on server start");
            List<CronJob> cronJobs = cronJobService.findAll();
            cronJobs.stream().forEach(cronJob -> {
                if (StringUtils.isNotEmpty(cronJob.getCronExpression()) &&
                        (cronJob.getJobStatus() == null || (cronJob.getJobStatus() != null &&
                                !cronJob.getJobStatus().equalsIgnoreCase("Stopped")))) {
                    logger.info("==QA rescheduling on server start " + cronJob.getId() + " initiated");
                    cronJobService.startCronJob(String.valueOf(cronJob.getId()), qualityAssuranceService);
                    logger.info("==QA rescheduling on server start " + cronJob.getId() + " scheduled");
                }
            });
            logger.info("==QA rescheduling on server end");
        }
    }
}
