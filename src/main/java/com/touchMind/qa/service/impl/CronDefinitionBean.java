package com.touchMind.qa.service.impl;

import com.touchMind.core.SpringContext;
import com.touchMind.core.mongo.model.CronHistory;
import com.touchMind.core.mongo.model.CronJob;
import com.touchMind.core.mongo.repository.CronHistoryRepository;
import com.touchMind.core.mongo.repository.CronRepository;
import com.touchMind.core.service.impl.CronService;
import com.touchMind.qa.utils.TestDataUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
@Service
public class CronDefinitionBean implements Runnable {
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String EXTENT_HTML = "_Extent.html";
    Logger logger = LoggerFactory.getLogger(CronDefinitionBean.class);
    CronHistoryRepository cronHistoryRepository;
    CronService cronService;
    CronRepository cronRepository;
    private CronDefinition cronDefinition;
    private ScheduledFuture scheduledFuture;
    private Environment env;

    @Autowired
    public CronDefinitionBean() {
        this.cronHistoryRepository = SpringContext.getBean(CronHistoryRepository.class);
        this.cronRepository = SpringContext.getBean(CronRepository.class);
        this.env = SpringContext.getBean(Environment.class);
    }

    public CronService getCronService() {
        return cronService;
    }

    public void setCronService(CronService cronService) {
        this.cronService = cronService;
    }

    @Override
    public void run() {
        String profile = "local";
        String[] profiles = env.getActiveProfiles();
        if (ArrayUtils.isNotEmpty(profiles)) {
            profile = profiles[0];
        }
        logger.debug("Profile - " + profile);
        CronJob cronJob = cronRepository.findByIdentifier(cronDefinition.getId());
        if (cronJob != null) {
            List<String> envProfiles = cronJob.getEnvProfiles();
            if ((CollectionUtils.isNotEmpty(envProfiles) && envProfiles.contains(profile)) || profile.equalsIgnoreCase("Prod")) {
                logger.info("Running cron : " + cronDefinition.getCronExpression());
                CronHistory cronHistory = new CronHistory();
                cronDefinition.setJobTime(df.format(new Date()));
                cronDefinition.setStatus("Running");
                try {
                    String currentTime = df.format(Calendar.getInstance().getTime());
                    String reportName = currentTime + EXTENT_HTML;
                    cronDefinition.getData().put(TestDataUtils.Field.REPORT_FILE_NAME.toString(), reportName);
                    cronDefinition.getData().put(TestDataUtils.Field.TITLE.toString(), cronDefinition.getTitle());
                    cronDefinition.getData().put(TestDataUtils.Field.EMAILS.toString(), cronDefinition.getEmail());
                    cronDefinition.getData().put(TestDataUtils.Field.JOB_TIME.toString(), cronDefinition.getJobTime());
                    cronDefinition.getData().put(TestDataUtils.Field.REPORT_URL.toString(), cronDefinition.getReportUrl());
                    //set here the session ID for all tests
                    UUID uuid = UUID.randomUUID();
                    String sessionId = uuid.toString();
                    cronDefinition.getData().put(TestDataUtils.Field.SESSION_ID.toString(), sessionId);
                    cronDefinition.getData().put(TestDataUtils.Field.JOB_TYPE.toString(), "cronJob");
                    logger.debug("===RUN " + cronDefinition.getId() + " : " + cronDefinition.getTitle() + " data process started");
                    logger.debug("===RUN DATA: " + cronDefinition.getData());
                    cronService.processData(cronDefinition.getData());
                    logger.debug("===RUN " + cronDefinition.getId() + " data process End");
                } catch (Exception e) {
                    cronDefinition.setJobTime(df.format(new Date()));
                    cronDefinition.setStatus("Failed");
                    logger.error(e.getMessage() + e);
                    cronHistory.setErrorMsg(e.getMessage());
                    saveCronHistory(cronDefinition, cronHistory);
                }
            } else {
                cronService.stopCronJob(cronDefinition.getId());
                logger.debug("Cronjob not executed as its not configured for profile -  : " + profile);
            }
        }
    }

    private void saveCronHistory(CronDefinition taskDefinition, CronHistory cronHistory) {
        cronHistory.setEmail(taskDefinition.getEmail());
        cronHistory.setJobTime(taskDefinition.getJobTime());
        cronHistory.setCronStatus(taskDefinition.getStatus());
        cronHistoryRepository.save(cronHistory);
    }

    public CronDefinition getCronDefinition() {
        return cronDefinition;
    }

}
