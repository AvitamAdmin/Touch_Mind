package com.touchMind.HealthcheckMvp;

import com.touchMind.core.mongo.model.CronJob;
import com.touchMind.core.mongo.model.SchedulerJob;
import com.touchMind.core.service.SchedulerJobService;
import com.touchMind.core.service.cronjob.CronJobService;
import com.touchMind.qa.service.QualityAssuranceService;
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
    private SchedulerJobService schedulerJobService;
    @Autowired
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    public void scheduleJobs() {
        List<CronTask> tasks = scheduledTaskRegistrar.getCronTaskList();

        if (CollectionUtils.isEmpty(tasks)) {
            logger.info("==TK rescheduling on server start");
            List<SchedulerJob> schedules = schedulerJobService.findAll();
            for (SchedulerJob schedule : schedules) {
                if (StringUtils.isNotEmpty(schedule.getCronExpression()) &&
                        (schedule.getJobStatus() == null || (schedule.getJobStatus() != null &&
                                !schedule.getJobStatus().equalsIgnoreCase("Stopped")))) {
                    logger.info("==TK rescheduling on server start " + schedule.getCronId() + " initiated");
                    schedulerJobService.startCronJob(String.valueOf(schedule.getId()));
                    logger.info("==TK rescheduling on server start " + schedule.getCronId() + " scheduled");
                }
            }
            logger.info("==TK rescheduling on server end");
        }

        if (CollectionUtils.isEmpty(tasks)) {
            logger.info("==QA rescheduling on server start");
            List<CronJob> cronJobs = cronJobService.findAll();
            cronJobs.stream().forEach(cronJob -> {
                if (StringUtils.isNotEmpty(cronJob.getCronExpression()) &&
                        (cronJob.getJobStatus() == null || (cronJob.getJobStatus() != null &&
                                !cronJob.getJobStatus().equalsIgnoreCase("Stopped")))) {
                    logger.info("==QA rescheduling on server start " + cronJob.getId() + " initiated");
                    cronJobService.startCronJob(String.valueOf(cronJob.getIdentifier()), qualityAssuranceService);
                    logger.info("==QA rescheduling on server start " + cronJob.getId() + " scheduled");
                }
            });
            logger.info("==QA rescheduling on server end");
        }
    }
}
