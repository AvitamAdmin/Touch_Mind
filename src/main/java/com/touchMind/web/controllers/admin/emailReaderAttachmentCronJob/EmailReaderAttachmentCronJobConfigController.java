package com.touchMind.web.controllers.admin.emailReaderAttachmentCronJob;

import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobDto;
import com.touchMind.core.mongo.dto.EmailAttachmentReaderCronJobWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SchedulerJobDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.EmailAttachmentReaderCronJob;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.EmailAttachmentReaderJobService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin/emailReaderAttachmentCronJob")
public class EmailReaderAttachmentCronJobConfigController extends BaseController {
    public static final String ADMIN_EMAIL_ATTACHMENT_READER = "/admin/emailReaderAttachmentCronJob";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Logger logger = LoggerFactory.getLogger(EmailReaderAttachmentCronJobConfigController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailAttachmentReaderJobService emailAttachmentReaderJobService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto getAllCronJobs(@RequestBody EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        Pageable pageable = getPageable(emailAttachmentReaderCronJobWsDto.getPage(), emailAttachmentReaderCronJobWsDto.getSizePerPage(), emailAttachmentReaderCronJobWsDto.getSortDirection(), emailAttachmentReaderCronJobWsDto.getSortField());
        EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto = CollectionUtils.isNotEmpty(emailAttachmentReaderCronJobWsDto.getEmailAttachmentReaderCronJobs()) ? emailAttachmentReaderCronJobWsDto.getEmailAttachmentReaderCronJobs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(emailAttachmentReaderCronJobDto, emailAttachmentReaderCronJobWsDto.getOperator());
        EmailAttachmentReaderCronJob emailAttachmentReaderCronJob = Objects.nonNull(emailAttachmentReaderCronJobDto) ? modelMapper.map(emailAttachmentReaderCronJobDto, EmailAttachmentReaderCronJob.class) : null;
        Page<EmailAttachmentReaderCronJob> page = Objects.nonNull(isSearchActive(emailAttachmentReaderCronJob)) && Objects.nonNull(emailAttachmentReaderCronJob) ? emailAttachmentReaderJobService.findAll(emailAttachmentReaderCronJob, exampleMatcher, pageable) : emailAttachmentReaderJobService.findAll(pageable);
        Type listType = new TypeToken<List<SchedulerJobDto>>() {
        }.getType();
        emailAttachmentReaderCronJobWsDto.setEmailAttachmentReaderCronJobs(modelMapper.map(page.getContent(), listType));
        emailAttachmentReaderCronJobWsDto.setBaseUrl(ADMIN_EMAIL_ATTACHMENT_READER);
        emailAttachmentReaderCronJobWsDto.setTotalPages(page.getTotalPages());
        emailAttachmentReaderCronJobWsDto.setTotalRecords(page.getTotalElements());
        emailAttachmentReaderCronJobWsDto.setAttributeList(getConfiguredAttributes(emailAttachmentReaderCronJobWsDto.getNode()));
        emailAttachmentReaderCronJobWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.EMAIL_ATTACHMENT_READER));
        return emailAttachmentReaderCronJobWsDto;
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new EmailAttachmentReaderCronJob());
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        return getConfiguredAttributes(emailAttachmentReaderCronJobWsDto.getNode());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.EMAIL_ATTACHMENT_READER);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody EmailAttachmentReaderCronJobDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(emailAttachmentReaderJobService.findByIdentifier(recordId), EmailAttachmentReaderCronJobDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto handleEdit(@RequestBody EmailAttachmentReaderCronJobWsDto request) {
        return emailAttachmentReaderJobService.handleEdit(request);
    }

    @PostMapping("/run")
    @ResponseBody
    public String runCronJob(@RequestBody EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto) {
        return emailAttachmentReaderJobService.runCronJob(emailAttachmentReaderCronJobDto);
    }


    @GetMapping("/copy")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto copy(@RequestParam("recordId") String recordId) {
        return emailAttachmentReaderJobService.copy(recordId);
    }

    @PostMapping("/getEdits")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto editMultiple(@RequestBody EmailAttachmentReaderCronJobWsDto request) {
        return emailAttachmentReaderJobService.editMultiple(request);
    }


    @PostMapping("/delete")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto deleteCronJob(@RequestBody EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        return emailAttachmentReaderJobService.deleteCronJob(emailAttachmentReaderCronJobWsDto);
    }

    @PostMapping("/startJob")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto startJob(@RequestBody EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        for (EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto : emailAttachmentReaderCronJobWsDto.getEmailAttachmentReaderCronJobs()) {
            emailAttachmentReaderJobService.startCronJob(emailAttachmentReaderCronJobDto.getIdentifier());
        }
        emailAttachmentReaderCronJobWsDto.setMessage("Cronjob started successfully!!");
        return emailAttachmentReaderCronJobWsDto;
    }

    @PostMapping("/stopJob")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto stopJob(@RequestBody EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto) {
        for (EmailAttachmentReaderCronJobDto emailAttachmentReaderCronJobDto : emailAttachmentReaderCronJobWsDto.getEmailAttachmentReaderCronJobs()) {
            emailAttachmentReaderJobService.stopCronJob(emailAttachmentReaderCronJobDto.getIdentifier());
        }
        emailAttachmentReaderCronJobWsDto.setMessage("Cronjob stopped successfully!!");
        return emailAttachmentReaderCronJobWsDto;
    }

    @PostMapping("/upload")
    public EmailAttachmentReaderCronJobWsDto uploadFile(@RequestBody MultipartFile file) {
        EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto = new EmailAttachmentReaderCronJobWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.EMAIL_ATTACHMENT_READER, EntityConstants.SUBSIDIARY, emailAttachmentReaderCronJobWsDto);
            if (StringUtils.isEmpty(emailAttachmentReaderCronJobWsDto.getMessage())) {
                emailAttachmentReaderCronJobWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return emailAttachmentReaderCronJobWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public EmailAttachmentReaderCronJobWsDto exportFile() {
        EmailAttachmentReaderCronJobWsDto emailAttachmentReaderCronJobWsDto = new EmailAttachmentReaderCronJobWsDto();
        try {
            emailAttachmentReaderCronJobWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.EMAIL_ATTACHMENT_READER, emailAttachmentReaderCronJobWsDto.getHeaderFields()));
            return emailAttachmentReaderCronJobWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
