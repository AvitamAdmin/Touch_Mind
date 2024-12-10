package com.cheil.core.service.cronjob.impl;

import com.cheil.core.mongo.dto.CronJobDto;
import com.cheil.core.mongo.dto.CronJobWsDto;
import com.cheil.core.mongo.dto.CronTestPlanDto;
import com.cheil.core.mongo.model.CronJob;
import com.cheil.core.mongo.repository.CronHistoryRepository;
import com.cheil.core.mongo.repository.CronRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CommonService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.cronjob.CronJobService;
import com.cheil.core.service.impl.CronService;
import com.cheil.form.CronForm;
import com.cheil.qa.service.impl.CronDefinition;
import com.cheil.qa.service.impl.CronDefinitionBean;
import com.cheil.qa.service.impl.CronSchedulingService;
import com.cheil.qa.utils.ServerStatus;
import org.apache.commons.lang3.StringUtils;
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
public class CronJobServiceImpl implements CronJobService {
    public static final String ADMIN_QA_CRONJOB = "/admin/qaCronJob";
    public static final String CRON_EXPRESSION = "cronExpression";
    public static final String EMAILS = "emails";
    public static final String CRON_ID = "cronId";
    public static final String REPORTS_PATH = "/reports/";
    public static final String ZERO = "0 ";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Logger logger = LoggerFactory.getLogger(CronJobServiceImpl.class);
    @Autowired
    CommonService commonService;
    @Autowired
    private CronRepository cronRepository;
    @Autowired
    private CronHistoryRepository cronHistoryRepository;
    @Autowired
    private CoreService coreService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CronSchedulingService cronSchedulingService;
    @Autowired
    private BaseService baseService;

    @Override
    public List<CronJob> findAll() {
        return cronRepository.findAll();
    }

    @Override
    public CronJob findCronJobById(String objectId) {
        CronJob cronJob = cronRepository.findByRecordId(objectId);
        return cronJob;
    }

    @Override
    public CronJob save(CronForm cronForm) {
        CronJob cronJob = null;
        if (cronForm.getRecordId() != null) {
            cronJob = cronRepository.findByRecordId(cronForm.getRecordId());
            modelMapper.map(cronForm, cronJob);
            cronJob.setCronTestPlanDtoList(removeEmptyPlans(cronJob));
            cronRepository.save(cronJob);
        } else {
            cronJob = modelMapper.map(cronForm, CronJob.class);
            cronJob.setLastModified(new Date());
            cronJob.setModifiedBy(coreService.getCurrentUser().getUsername());
            cronJob.setCronTestPlanDtoList(removeEmptyPlans(cronJob));
            cronJob.setJobStatus(ServerStatus.STOPPED.status);
            cronRepository.save(cronJob);
        }
        return cronJob;
    }

    @Override
    public void stopCronJob(String id) {
        cronSchedulingService.killOldJob(id);
        CronJob cronJob = findCronJobById(id);
        cronJob.setJobStatus(ServerStatus.STOPPED.status);
        logger.debug("==QA stopCronJob " + id + " CHANGED status to " + ServerStatus.STOPPED.status);
        save(cronJob);
    }

    private List<CronTestPlanDto> removeEmptyPlans(CronJob cronJob) {
        List<CronTestPlanDto> correctPlans = new ArrayList<>();
        cronJob.getCronTestPlanDtoList().forEach(cronTestPlanForm -> {
            if (StringUtils.isNotEmpty(cronTestPlanForm.getTestPlan())) {
                correctPlans.add(cronTestPlanForm);
            }
        });
        return correctPlans;
    }

    @Override
    public void deleteCronJobById(String id) {
        cronSchedulingService.killOldJob(id);
        logger.debug("==QA deleteCronJobById " + id + " started");
        cronRepository.deleteByRecordId(id);
        logger.debug("==QA deleteCronJobById " + id + " deleted");
    }

    @Override
    public CronJob save(CronJob cronJob) {
        return cronRepository.save(cronJob);
    }

    @Override
    public CronDefinition getTaskDefinition(Map<String, String> data, String id) {
        CronDefinition cronDefinition = new CronDefinition();
        cronDefinition.setCronExpression(ZERO + data.get(CRON_EXPRESSION));
        cronDefinition.setEmail(data.get(EMAILS));
        cronDefinition.setTitle(data.get(CRON_ID));
        cronDefinition.setId(id);
        cronDefinition.setJobTime(df.format(new Date()));
        cronDefinition.setStatus(ServerStatus.SCHEDULED.status);
        cronDefinition.setData(data);
        return cronDefinition;
    }

    @Override
    public void startCronJob(String id, CronService qualityAssuranceService) {
        cronSchedulingService.killOldJob(id);
        CronJob cronJob = findCronJobById(id);
        CronDefinition cronDefinition = getTaskDefinition(commonService.toMap(cronJob), id);
        CronDefinitionBean taskDefinitionBean = new CronDefinitionBean();
        taskDefinitionBean.setCronService(qualityAssuranceService);
        cronDefinition.setReportUrl(cronJob.getSiteUrl() + REPORTS_PATH);
        taskDefinitionBean.setCronDefinition(cronDefinition);
        logger.debug("==QA startCronJob " + id + " : " + cronJob.getIdentifier() + " : " + cronJob.getCronExpression() + " scheduling");
        cronSchedulingService.scheduleATask(taskDefinitionBean);
        logger.debug("==QA startCronJob " + id + " : " + cronJob.getIdentifier() + " : " + cronJob.getCronExpression() + ServerStatus.SCHEDULED.status);
        cronJob.setJobStatus(ServerStatus.SCHEDULED.status);
        cronRepository.save(cronJob);
        logger.debug("==QA startCronJob " + id + " : " + cronJob.getIdentifier() + " : " + cronJob.getCronExpression() + " status changed");
    }

    @Override
    public CronJobWsDto handleEdit(CronJobWsDto request) {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        CronJob requestData = null;
        List<CronJobDto> cronJobs = request.getCronJobs();
        List<CronJob> cronJobList = new ArrayList<>();
        for (CronJobDto cronJob : cronJobs) {
            if (cronJob.getRecordId() != null) {
                requestData = cronRepository.findByRecordId(cronJob.getRecordId());
                modelMapper.map(cronJob, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.CRONJOB, cronJob.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(cronJob, CronJob.class);
            }
            baseService.populateCommonData(requestData);
            cronRepository.save(requestData);
            if (cronJob.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            cronRepository.save(requestData);
            cronJobList.add(requestData);
            cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        }
        cronJobWsDto.setCronJobs(modelMapper.map(cronJobList, List.class));
        return cronJobWsDto;
    }
}
