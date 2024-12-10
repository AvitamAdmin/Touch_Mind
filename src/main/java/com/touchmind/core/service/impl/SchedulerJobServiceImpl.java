package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SchedulerJobDto;
import com.touchmind.core.mongo.dto.SchedulerJobWsDto;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CommonService;
import com.touchmind.core.service.CoreService;
import com.touchmind.qa.service.impl.CronDefinition;
import com.touchmind.qa.service.impl.CronDefinitionBean;
import com.touchmind.qa.service.impl.CronSchedulingService;
import com.touchmind.tookit.service.ReportService;
import com.touchmind.core.mongo.model.SchedulerJob;
import com.touchmind.core.mongo.repository.SchedulerJobRepository;
import com.touchmind.core.service.SchedulerJobService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SchedulerJobServiceImpl implements SchedulerJobService {

    public static final String ADMIN_SCHEDULER = "/admin/scheduler";
    public static final String CRON_EXPRESSION = "cronExpression";
    public static final String EMAILS = "emails";
    public static final String CRON_ID = "cronId";
    public static final String SCHEDULED = "Scheduled";
    public static final String REPORTS_PATH = "/reports/";
    public static final String STOPPED = "Stopped";
    public static final String ZERO = "0 ";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Logger logger = LoggerFactory.getLogger(SchedulerJobServiceImpl.class);
    @Autowired
    private SchedulerJobRepository schedulerJobRepository;
    @Autowired
    private CronSchedulingService cronSchedulingService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Override
    public SchedulerJob findCronJobById(String objectId) {
        return schedulerJobRepository.findByRecordId(objectId);
    }

    @Override
    public SchedulerJob findByRecordId(String recordId) {
        return schedulerJobRepository.findByRecordId(recordId);
    }


    @Override
    public void deleteCronJobById(String id) {
        cronSchedulingService.killOldJob(id);
        logger.info("==TK deleteCronJobById " + id + " deleting");
        schedulerJobRepository.deleteByRecordId(id);
        logger.info("==TK deleteCronJobById " + id + " deleted");
    }

    @Override
    public List<SchedulerJob> findAll() {
        return schedulerJobRepository.findAll();
    }

    @Override
    public SchedulerJob save(SchedulerJob cronJob) {
        return schedulerJobRepository.save(cronJob);
    }

    @Override
    public void startCronJob(String id) {
        SchedulerJob cronJob = findCronJobById(id);
        logger.info("==TK startCronJob " + id + " cancel if old job running");
        cronSchedulingService.killOldJob(id);
        logger.info("==TK startCronJob " + id + " cancel process ended");
        Map<String, String> data = commonService.toMap(cronJob);
        CronDefinition taskDefinition = getTaskDefinition(data, id);
        taskDefinition.setReportUrl(data.get("siteUrl") + REPORTS_PATH);
        CronDefinitionBean taskDefinitionBean = new CronDefinitionBean();
        taskDefinitionBean.setCronService(reportService);
        taskDefinitionBean.setCronDefinition(taskDefinition);
        logger.info("==TK startCronJob " + id + " scheduling the cronjob");
        cronSchedulingService.scheduleATask(taskDefinitionBean);
        logger.info("==TK startCronJob " + id + " scheduled");
        cronJob.setJobStatus(SCHEDULED);
        schedulerJobRepository.save(cronJob);
        logger.info("==TK startCronJob " + id + " status changed to " + SCHEDULED);
    }

    @Override
    public void stopCronJob(String id) {
        cronSchedulingService.killOldJob(id);
        SchedulerJob schedule = findCronJobById(id);
        schedule.setJobStatus(STOPPED);
        logger.info("==TK stopCronJob " + id + " changed status to " + STOPPED);
        schedulerJobRepository.save(schedule);
    }

    @Override
    public SchedulerJobWsDto handleEdit(SchedulerJobWsDto request) {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        SchedulerJob requestData = null;
        List<SchedulerJobDto> schedulerJobs = request.getSchedulerJobs();
        List<SchedulerJob> schedulerJobList = new ArrayList<>();
        for (SchedulerJobDto schedulerJob : schedulerJobs) {
            if (schedulerJob.getRecordId() != null) {
                requestData = schedulerJobRepository.findByRecordId(schedulerJob.getRecordId());
                modelMapper.map(schedulerJob, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SCHEDULER, schedulerJob.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(schedulerJob, SchedulerJob.class);
            }
            baseService.populateCommonData(requestData);
            schedulerJobRepository.save(requestData);
            if (schedulerJob.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            schedulerJobRepository.save(requestData);
            schedulerJobList.add(requestData);
            schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        }
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(schedulerJobList, List.class));
        return schedulerJobWsDto;
    }

    private CronDefinition getTaskDefinition(Map<String, String> data, String id) {
        CronDefinition taskDefinition = new CronDefinition();
        taskDefinition.setCronExpression(ZERO + data.get(CRON_EXPRESSION));
        taskDefinition.setEmail(data.get(EMAILS));
        taskDefinition.setTitle(data.get(CRON_ID));
        taskDefinition.setId(id);
        taskDefinition.setJobTime(df.format(new Date()));
        taskDefinition.setStatus(SCHEDULED);
        taskDefinition.setData(data);
        return taskDefinition;
    }

}
