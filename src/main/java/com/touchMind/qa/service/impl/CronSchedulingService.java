package com.touchMind.qa.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Service
public class CronSchedulingService {
    private final Logger logger = LoggerFactory.getLogger(CronSchedulingService.class);
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    private ScheduledTaskRegistrar scheduledTaskRegistrar;

    //private final Map<String, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

    @Async
    public void scheduleATask(Runnable tasklet) {
        logger.info("======== Central cron job canceling old job ! =======");
        List<CronTask> tasks = scheduledTaskRegistrar.getCronTaskList();
        CronDefinitionBean currentTaskBean = (CronDefinitionBean) tasklet;
        for (CronTask task : tasks) {
            CronDefinitionBean taskBean = (CronDefinitionBean) task.getRunnable();
            if (taskBean.getCronDefinition().getId().equalsIgnoreCase(currentTaskBean.getCronDefinition().getId())) {
                if (!taskBean.getScheduledFuture().isCancelled()) {
                    logger.info("======== scheduleATask old task " + currentTaskBean.getCronDefinition().getId() + " cancel =======");
                    taskBean.getScheduledFuture().cancel(true);
                    logger.info("======== scheduleATask old task " + currentTaskBean.getCronDefinition().getId() + " canceled =======");
                }
            }
        }

        CronDefinitionBean job = (CronDefinitionBean) tasklet;
        CronTask cronTask = new CronTask(tasklet, new CronTrigger(currentTaskBean.getCronDefinition().getCronExpression(), TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        logger.info("======== scheduleATask " + job.getCronDefinition().getId() + " rescheduling =======");
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(tasklet, new CronTrigger(currentTaskBean.getCronDefinition().getCronExpression(), TimeZone.getTimeZone(TimeZone.getDefault().getID())));


        logger.info("======== scheduleATask " + job.getCronDefinition().getId() + " rescheduled =======");
        scheduledTaskRegistrar.addCronTask(cronTask);
        //scheduledTasks.put(currentTaskBean.getCronDefinition().getId(), scheduledFuture);
        logger.info("======== scheduleATask " + job.getCronDefinition().getId() + " registered to scheduledTaskRegistrar =======");
        job.setScheduledFuture(scheduledFuture);
        logger.info("======== Central cron job Scheduled! =======");
    }

    public void killOldJob(String id) {
        List<CronTask> tasks = scheduledTaskRegistrar.getCronTaskList();
        logger.info("==Clean cron task " + id + " started");
        for (CronTask task : tasks) {
            CronDefinitionBean taskBean = (CronDefinitionBean) task.getRunnable();
            if (taskBean.getCronDefinition().getId().equalsIgnoreCase(id)) {
                if (!taskBean.getScheduledFuture().isCancelled()) {
                    logger.info("Clean cron task " + id + " in progress");
                    taskBean.getScheduledFuture().cancel(true);
                    logger.info("Clean cron task " + id + " stopped");
                }
            }
        }
        logger.info("==Clean cron task " + id + " done");
    }
}
