package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.CronJobDto;
import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobDto;
import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobWsDto;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.model.EmailAttachmentReaderCronJob;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EmailReaderAttachmentCronJobRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CommonService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.EmailAttachmentReaderJobService;
import com.touchMind.mail.service.EmailReceiverService;
import com.touchMind.qa.service.impl.CronDefinition;
import com.touchMind.qa.service.impl.CronDefinitionBean;
import com.touchMind.qa.service.impl.CronSchedulingService;
import com.touchMind.qa.utils.TestDataUtils;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailAttachmentReaderJobServiceImpl implements EmailAttachmentReaderJobService {

    public static final String ADMIN_EMAIL_ATTACHMENT_READER = "/admin/emailReaderAttachmentCronJob";
    public static final String CRON_EXPRESSION = "cronExpression";
    public static final String EMAILS = "emails";
    public static final String CRON_ID = "cronId";
    public static final String SCHEDULED = "Scheduled";
    public static final String REPORTS_PATH = "/reports/";
    public static final String STOPPED = "Stopped";
    public static final String ZERO = "0 ";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String SOURCE_ADDRESS = "sourceAddress";
    public static final String CURRENT_SESSION_ID = "currentSessionId";
    private final Logger LOG = LoggerFactory.getLogger(EmailAttachmentReaderJobServiceImpl.class);
    @Autowired
    private EmailReaderAttachmentCronJobRepository emailReaderAttachmentCronJobRepository;
    @Autowired
    private CronSchedulingService cronSchedulingService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private EmailReceiverService emailReceiverService;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Override
    public EmailAttachmentReaderCronJob findCronJobById(String objectId) {
        return emailReaderAttachmentCronJobRepository.findByIdentifier(objectId);
    }

    @Override
    public EmailAttachmentReaderCronJob findByIdentifier(String recordId) {
        return emailReaderAttachmentCronJobRepository.findByIdentifier(recordId);
    }


    @Override
    public void deleteCronJobById(String id) {
        stopCronJob(id);
        LOG.info("==TK deleteCronJobById " + id + " deleting");
        emailReaderAttachmentCronJobRepository.deleteByIdentifier(id);
        LOG.info("==TK deleteCronJobById " + id + " deleted");
    }

    @Override
    public List<EmailAttachmentReaderCronJob> findAll() {
        return emailReaderAttachmentCronJobRepository.findAll();
    }

    @Override
    public Page<EmailAttachmentReaderCronJob> findAll(EmailAttachmentReaderCronJob cronJob, ExampleMatcher exampleMatcher, Pageable pageable) {
        return emailReaderAttachmentCronJobRepository.findAll(Example.of(cronJob, exampleMatcher), pageable);
    }

    @Override
    public Page<EmailAttachmentReaderCronJob> findAll(Pageable pageable) {
        return emailReaderAttachmentCronJobRepository.findAll(pageable);
    }

    @Override
    public EmailAttachmentReaderCronJob save(EmailAttachmentReaderCronJob cronJob) {
        return emailReaderAttachmentCronJobRepository.save(cronJob);
    }

    @Override
    public void startCronJob(String id) {
        EmailAttachmentReaderCronJob cronJob = findCronJobById(id);
        Optional.ofNullable(cronJob).ifPresent(job -> {
            LOG.info("==TK startCronJob " + id + " cancel if old job running");
            cronSchedulingService.killOldJob(id);
            LOG.info("==TK startCronJob " + id + " cancel process ended");
            Map<String, String> data = commonService.toMap(job);
            CronDefinition taskDefinition = getTaskDefinition(data, id);
            taskDefinition.setReportUrl(data.get("siteUrl") + REPORTS_PATH);
            CronDefinitionBean taskDefinitionBean = new CronDefinitionBean();
            taskDefinitionBean.setCronService(emailReceiverService);
            taskDefinitionBean.setCronDefinition(taskDefinition);
            LOG.info("==TK startCronJob " + id + " scheduling the cronjob");
            cronSchedulingService.scheduleATask(taskDefinitionBean);
            LOG.info("==TK startCronJob " + id + " scheduled");
            job.setJobStatus(SCHEDULED);
            emailReaderAttachmentCronJobRepository.save(job);
            LOG.info("==TK startCronJob " + id + " status changed to " + SCHEDULED);
        });
    }

    @Override
    public void stopCronJob(String id) {
        cronSchedulingService.killOldJob(id);
        EmailAttachmentReaderCronJob cronJob = findCronJobById(id);
        Optional.ofNullable(cronJob).ifPresent(job -> {
            job.setJobStatus(STOPPED);
            LOG.info("==TK stopCronJob " + id + " changed status to " + STOPPED);
            emailReaderAttachmentCronJobRepository.save(cronJob);
        });
    }

    @Override
    public EmailAttachmentReaderCronJobWsDto handleEdit(EmailAttachmentReaderCronJobWsDto request) {
        EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto = new EmailAttachmentReaderCronJobWsDto();
        List<EmailAttachmentReaderCronJobDto> emailAttachmentReaderCronJobs = request.getEmailAttachmentReaderCronJobs();
        List<EmailAttachmentReaderCronJobDto> emailAttachmentReaderCronJobList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(emailAttachmentReaderCronJobs)) {
            for (EmailAttachmentReaderCronJobDto job : emailAttachmentReaderCronJobs) {
                EmailAttachmentReaderCronJob newOrEditedJob = null;
                if (job.isAdd() && baseService.validateIdentifier(EntityConstants.EMAIL_ATTACHMENT_READER, job.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                newOrEditedJob = emailReaderAttachmentCronJobRepository.findByIdentifier(job.getIdentifier());
                if (newOrEditedJob != null) {
                    modelMapper.map(job, newOrEditedJob);
                } else {
                    newOrEditedJob = modelMapper.map(job, EmailAttachmentReaderCronJob.class);
                }
                baseService.populateCommonData(newOrEditedJob);
                emailReaderAttachmentCronJobRepository.save(newOrEditedJob);
                stopCronJob(newOrEditedJob.getIdentifier());
                emailAttachmentReaderCronJobList.add(modelMapper.map(newOrEditedJob, EmailAttachmentReaderCronJobDto.class));
                emailAttachmentReaderCronJobWsDto.setBaseUrl(ADMIN_EMAIL_ATTACHMENT_READER);
            }
        }
        Type listType = new TypeToken<List<EmailAttachmentReaderCronJobDto>>() {
        }.getType();
        emailAttachmentReaderCronJobWsDto.setEmailAttachmentReaderCronJobs(modelMapper.map(emailAttachmentReaderCronJobList, listType));
        emailAttachmentReaderCronJobWsDto.setMessage("Email Reader Attachment Job updated successfully!!");
        return emailAttachmentReaderCronJobWsDto;
    }

    @Override
    public String runCronJob(EmailAttachmentReaderCronJobDto request) {
        EmailAttachmentReaderCronJob cronJob = findByIdentifier(request.getIdentifier());
        SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(cronJob.getMapping());
        DataSource dataSource = (CollectionUtils.isNotEmpty(sourceTargetMapping.getSourceTargetParamMappings()) && Objects.nonNull(sourceTargetMapping.getSourceTargetParamMappings().getFirst().getDataSource()))
                ? dataSourceRepository.findByIdentifier(sourceTargetMapping.getSourceTargetParamMappings().getFirst().getDataSource()) : null;
        Map<String, String> data = commonService.toMap(cronJob);
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();
        data.put(TestDataUtils.Field.SESSION_ID.toString(), sessionId);
        data.put(TestDataUtils.Field.JOB_TIME.toString(), df.format(new Date()));
        data.put(SOURCE_ADDRESS, Objects.nonNull(dataSource) ? dataSource.getSourceAddress() : StringUtils.EMPTY);
        data.put(CURRENT_SESSION_ID, cronJob.getIdentifier());
        try {
            emailReceiverService.processData(data);
            return "Success";
        } catch (Exception e) {
            LOG.error(e.toString());
            return "Error";
        }

    }

    @Override
    public EmailAttachmentReaderCronJobWsDto copy(String recordId) {
        EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto = new EmailAttachmentReaderCronJobWsDto();
        EmailAttachmentReaderCronJob cronJob = emailReaderAttachmentCronJobRepository.findByIdentifier(recordId);
        CronJobDto cronJobDto = modelMapper.map(cronJob, CronJobDto.class);
        cronJobDto.setIdentifier(null);
        cronJobDto.setCreationTime(new Date());
        cronJobDto.setLastModified(new Date());
        cronJobDto.setIdentifier("Copy_" + cronJob.getIdentifier() + cronJobDto.getCreationTime());
        EmailAttachmentReaderCronJob clonedCronjob = modelMapper.map(cronJobDto, EmailAttachmentReaderCronJob.class);
        emailReaderAttachmentCronJobRepository.save(clonedCronjob);
        clonedCronjob.setIdentifier(String.valueOf(clonedCronjob.getId().getTimestamp()));
        emailReaderAttachmentCronJobRepository.save(clonedCronjob);
        emailAttachmentReaderCronJobWsDto.setMessage("Cronjob cloned successfully!!");
        return emailAttachmentReaderCronJobWsDto;
    }

    @Override
    public EmailAttachmentReaderCronJobWsDto editMultiple(EmailAttachmentReaderCronJobWsDto request) {
        EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto = new EmailAttachmentReaderCronJobWsDto();
        List<EmailAttachmentReaderCronJob> emailAttachmentReaderCronJobs = new ArrayList<>();
        for (EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto : request.getEmailAttachmentReaderCronJobs()) {
            emailAttachmentReaderCronJobs.add(emailReaderAttachmentCronJobRepository.findByIdentifier(emailAttachmentReaderCronJobDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<EmailAttachmentReaderCronJobDto>>() {
        }.getType();
        emailAttachmentReaderCronJobWsDto.setEmailAttachmentReaderCronJobs(modelMapper.map(emailAttachmentReaderCronJobs, listType));
        emailAttachmentReaderCronJobWsDto.setRedirectUrl("/admin/role");
        emailAttachmentReaderCronJobWsDto.setBaseUrl(ADMIN_EMAIL_ATTACHMENT_READER);
        return emailAttachmentReaderCronJobWsDto;
    }

    @Override
    public EmailAttachmentReaderCronJobWsDto deleteCronJob(EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        for (EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto : emailAttachmentReaderCronJobWsDto.getEmailAttachmentReaderCronJobs()) {
            stopCronJob(emailAttachmentReaderCronJobDto.getIdentifier());
            emailReaderAttachmentCronJobRepository.deleteByIdentifier(emailAttachmentReaderCronJobDto.getIdentifier());
        }
        emailAttachmentReaderCronJobWsDto.setBaseUrl(ADMIN_EMAIL_ATTACHMENT_READER);
        emailAttachmentReaderCronJobWsDto.setMessage("Data deleted successfully!!");
        return emailAttachmentReaderCronJobWsDto;
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
