package com.touchMind.web.controllers.admin.scheduler;

import com.touchMind.core.mongo.dto.CronJobDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SchedulerJobDto;
import com.touchMind.core.mongo.dto.SchedulerJobWsDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.SchedulerJob;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.SchedulerJobRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CommonService;
import com.touchMind.core.service.SchedulerJobService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.form.SchedulerForm;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.tookit.service.ReportService;
import com.touchMind.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/scheduler")
public class SchedulerConfigController extends BaseController {
    public static final String ADMIN_SCHEDULER = "/admin/scheduler";
    public static final String STOPPED = "Stopped";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Logger logger = LoggerFactory.getLogger(SchedulerConfigController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private SchedulerJobService schedulerJobService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SchedulerJobRepository schedulerJobRepository;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private BaseService baseService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @PostMapping
    @ResponseBody
    public SchedulerJobWsDto getAllCronJobs(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        Pageable pageable = getPageable(schedulerJobWsDto.getPage(), schedulerJobWsDto.getSizePerPage(), schedulerJobWsDto.getSortDirection(), schedulerJobWsDto.getSortField());
        SchedulerJobDto schedulerJobDto = CollectionUtils.isNotEmpty(schedulerJobWsDto.getSchedulerJobs()) ? schedulerJobWsDto.getSchedulerJobs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(schedulerJobDto, schedulerJobWsDto.getOperator());
        SchedulerJob schedulerJob = schedulerJobDto != null ? modelMapper.map(schedulerJobDto, SchedulerJob.class) : null;
        Page<SchedulerJob> page = isSearchActive(schedulerJob) != null ? schedulerJobRepository.findAll(Example.of(schedulerJob, exampleMatcher), pageable) : schedulerJobRepository.findAll(pageable);
        Type listType = new TypeToken<List<SchedulerJobDto>>() {
        }.getType();
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(page.getContent(), listType));
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        schedulerJobWsDto.setTotalPages(page.getTotalPages());
        schedulerJobWsDto.setTotalRecords(page.getTotalElements());
        schedulerJobWsDto.setAttributeList(getConfiguredAttributes(schedulerJobWsDto.getNode()));
        schedulerJobWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.SCHEDULER));
        return schedulerJobWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new SchedulerJob());
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        return getConfiguredAttributes(schedulerJobWsDto.getNode());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SCHEDULER);
    }

    @GetMapping("/get")
    @ResponseBody
    public SchedulerJobWsDto getActiveSchedulers() {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        Type listType = new TypeToken<List<SchedulerJobDto>>() {
        }.getType();
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(schedulerJobRepository.findByStatusOrderByIdentifier(true), listType));
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        return schedulerJobWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody SchedulerJobDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(schedulerJobRepository.findByIdentifier(recordId), SchedulerJobDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public SchedulerJobWsDto handleEdit(@RequestBody SchedulerJobWsDto request) {
        return schedulerJobService.handleEdit(request);
    }

    @PostMapping("/run")
    @ResponseBody
    public String handleEditOne(@RequestBody SchedulerJobDto schedulerJobDto) {
        SchedulerJob cronJob = schedulerJobService.findByIdentifier(schedulerJobDto.getIdentifier());
        Map<String, String> data = commonService.toMap(cronJob);
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();
        data.put(TestDataUtils.Field.SESSION_ID.toString(), sessionId);
        data.put(TestDataUtils.Field.JOB_TIME.toString(), df.format(new Date()));
        reportService.processData(data);
        return "Success";
    }

    private SchedulerJob getScheduler(SchedulerForm schedulerForm, Node node) {
        SchedulerJob cronJob = StringUtils.isNotEmpty(schedulerForm.getIdentifier()) ? schedulerJobService.findByIdentifier(schedulerForm.getIdentifier()) : modelMapper.map(schedulerForm, SchedulerJob.class);
        cronJob.setShortcuts(StringUtils.join(schedulerForm.getShortcuts(), ","));
        cronJob.setSites(schedulerForm.getSites());
        cronJob.setCronId(node.getIdentifier());
        cronJob.setNodePath(node.getPath());
        cronJob.setSkus(schedulerForm.getSkus());
        cronJob.setCronExpression(schedulerForm.getCronExpression());
        cronJob.setMapping(schedulerForm.getMapping());
        cronJob.setSubsidiary(schedulerForm.getSubsidiary());
        cronJob.setEmails(schedulerForm.getEmails());
        cronJob.setEnableHistory(schedulerForm.getEnableHistory());
        cronJob.setVoucherCode(schedulerForm.getVoucherCode());
        cronJob.setStatus(schedulerForm.getStatus());
        cronJob.setJobStatus(StringUtils.isEmpty(cronJob.getJobStatus()) ? STOPPED : cronJob.getJobStatus());
        //TODO Check the interface name is correct ?
        cronJob.setInterfaceName(schedulerForm.getId());
        return cronJob;
    }

    @GetMapping("/copy")
    @ResponseBody
    public SchedulerJobWsDto copy(@RequestParam("recordId") String recordId) {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        SchedulerJob cronJob = schedulerJobRepository.findByIdentifier(recordId);
        CronJobDto cronJobDto = modelMapper.map(cronJob, CronJobDto.class);
        cronJobDto.setIdentifier(null);
        cronJobDto.setCreationTime(new Date());
        cronJobDto.setLastModified(new Date());
        cronJobDto.setIdentifier("Copy_" + cronJob.getIdentifier());
        SchedulerJob clonedCronjob = modelMapper.map(cronJobDto, SchedulerJob.class);
        schedulerJobRepository.save(clonedCronjob);
        clonedCronjob.setIdentifier(String.valueOf(clonedCronjob.getId().getTimestamp()));
        schedulerJobRepository.save(clonedCronjob);
        schedulerJobWsDto.setMessage("Cronjob cloned successfully!!");
        return schedulerJobWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SchedulerJobWsDto editMultiple(@RequestBody SchedulerJobWsDto request) {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        List<SchedulerJob> schedulerJobs = new ArrayList<>();
        for (SchedulerJobDto schedulerJobDto : request.getSchedulerJobs()) {
            schedulerJobs.add(schedulerJobRepository.findByIdentifier(schedulerJobDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<SchedulerJobDto>>() {
        }.getType();
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(schedulerJobs, listType));
        schedulerJobWsDto.setRedirectUrl("/admin/role");
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        return schedulerJobWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public SchedulerJobWsDto deleteScheduler(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        for (SchedulerJobDto schedulerJobDto : schedulerJobWsDto.getSchedulerJobs()) {
            schedulerJobService.stopCronJob(schedulerJobDto.getIdentifier());
            schedulerJobRepository.deleteByIdentifier(schedulerJobDto.getIdentifier());
        }
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        schedulerJobWsDto.setMessage("Data deleted successfully!!");
        return schedulerJobWsDto;
    }

    @GetMapping("/isVoucherEnabled/{mappingId}")
    public @ResponseBody Boolean isVoucherEnabled(@PathVariable("mappingId") String mappingId) {
        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(mappingId);
        return mapping.getEnableVoucher() != null ? mapping.getEnableVoucher() : Boolean.valueOf(false);
    }

    @PostMapping("/startJob")
    @ResponseBody
    public SchedulerJobWsDto startJob(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        for (SchedulerJobDto schedulerJobDto : schedulerJobWsDto.getSchedulerJobs()) {
            schedulerJobService.startCronJob(schedulerJobDto.getIdentifier());
        }
        schedulerJobWsDto.setMessage("Cronjob started successfully!!");
        return schedulerJobWsDto;
    }

    @PostMapping("/stopJob")
    @ResponseBody
    public SchedulerJobWsDto stopJob(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        for (SchedulerJobDto schedulerJobDto : schedulerJobWsDto.getSchedulerJobs()) {
            schedulerJobService.stopCronJob(schedulerJobDto.getIdentifier());
        }
        schedulerJobWsDto.setMessage("Cronjob stopped successfully!!");
        return schedulerJobWsDto;
    }

    @PostMapping("/upload")
    public SchedulerJobWsDto uploadFile(@RequestBody MultipartFile file) {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SCHEDULER, EntityConstants.SUBSIDIARY, schedulerJobWsDto);
            if (StringUtils.isEmpty(schedulerJobWsDto.getMessage())) {
                schedulerJobWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return schedulerJobWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public SchedulerJobWsDto uploadFile(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {

        try {
            schedulerJobWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SCHEDULER, schedulerJobWsDto.getHeaderFields()));
            return schedulerJobWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
