package com.cheil.qa.service.impl;

import com.cheil.core.SpringContext;
import com.cheil.core.mongo.model.CronHistory;
import com.cheil.core.mongo.model.CronJob;
import com.cheil.core.mongo.repository.CronHistoryRepository;
import com.cheil.core.mongo.repository.CronRepository;
import com.cheil.core.service.impl.CronService;
import com.cheil.qa.utils.TestDataUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    public CronDefinitionBean() {
        this.cronHistoryRepository = SpringContext.getBean(CronHistoryRepository.class);
        this.cronRepository = SpringContext.getBean(CronRepository.class);
    }

    public CronService getCronService() {
        return cronService;
    }

    public void setCronService(CronService cronService) {
        this.cronService = cronService;
    }

    @Override
    public void run() {
        String profile = System.getProperty("spring.profiles.active");
        Optional<CronJob> cronJob = cronRepository.findById(new ObjectId(cronDefinition.getId()));
        if (cronJob.isPresent()) {
            List<String> envProfiles = cronJob.get().getEnvProfiles();
            if ((StringUtils.isNotEmpty(profile) && CollectionUtils.isNotEmpty(envProfiles) && envProfiles.contains(profile)) || (StringUtils.isEmpty(profile) || CollectionUtils.isEmpty(envProfiles))) {
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
            }
        } else {
            logger.info("Cronjob not executed as its not configured for profile -  : " + profile);
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
