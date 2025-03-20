package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobDto;
import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobWsDto;
import com.touchMind.core.mongo.model.EmailAttachmentReaderCronJob;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmailAttachmentReaderJobService {
    EmailAttachmentReaderCronJob findCronJobById(String objectId);

    EmailAttachmentReaderCronJob findByIdentifier(String recordId);

    void deleteCronJobById(String id);

    List<EmailAttachmentReaderCronJob> findAll();

    Page<EmailAttachmentReaderCronJob> findAll(EmailAttachmentReaderCronJob cronJob, ExampleMatcher exampleMatcher, Pageable pageable);

    Page<EmailAttachmentReaderCronJob> findAll(Pageable pageable);

    EmailAttachmentReaderCronJob save(EmailAttachmentReaderCronJob cronJob);

    void startCronJob(String id);

    void stopCronJob(String id);

    EmailAttachmentReaderCronJobWsDto handleEdit(EmailAttachmentReaderCronJobWsDto request);

    String runCronJob(EmailAttachmentReaderCronJobDto request);

    EmailAttachmentReaderCronJobWsDto copy(String recordId);

    EmailAttachmentReaderCronJobWsDto editMultiple(EmailAttachmentReaderCronJobWsDto request);

    EmailAttachmentReaderCronJobWsDto deleteCronJob(EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto);
}
