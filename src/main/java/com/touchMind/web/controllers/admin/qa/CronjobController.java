package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.CronJobDto;
import com.touchMind.core.mongo.dto.CronJobWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.CronJob;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.CronJobProfileRepository;
import com.touchMind.core.mongo.repository.CronRepository;
import com.touchMind.core.mongo.repository.DashboardRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.EnvironmentRepository;
import com.touchMind.core.mongo.repository.QaTestPlanRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.TestProfileRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CommonService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SiteService;
import com.touchMind.core.service.TestPlanService;
import com.touchMind.core.service.cronjob.CronJobService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.qa.service.QualityAssuranceService;
import com.touchMind.qa.utils.TestDataUtils;
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
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/admin/qaCronJob")
public class CronjobController extends BaseController {

    public static final String ADMIN_QA_CRONJOB = "/admin/qaCronJob";
    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Logger logger = LoggerFactory.getLogger(CronjobController.class);
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private CronJobService cronJobService;
    @Autowired
    private QualityAssuranceService qualityAssuranceService;
    @Autowired
    private SiteService siteService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
//    @Autowired
//    private SubsidiaryService subsidiaryService;
    @Autowired
    private TestProfileRepository testProfileRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;
    @Autowired
    private CronRepository cronRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private CoreService coreService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private SiteRepository siteRepository;

    @PostMapping
    @ResponseBody
    public CronJobWsDto getAllCronJobs(@RequestBody CronJobWsDto cronJobWsDto) {
        Pageable pageable = getPageable(cronJobWsDto.getPage(), cronJobWsDto.getSizePerPage(), cronJobWsDto.getSortDirection(), cronJobWsDto.getSortField());
        CronJobDto cronJobDto = CollectionUtils.isNotEmpty(cronJobWsDto.getCronJobs()) ? cronJobWsDto.getCronJobs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(cronJobDto, cronJobWsDto.getOperator());
        CronJob cronJob = cronJobDto != null ? modelMapper.map(cronJobDto, CronJob.class) : null;
        Page<CronJob> page = isSearchActive(cronJob) != null ? cronRepository.findAll(Example.of(cronJob, exampleMatcher), pageable) : cronRepository.findAll(pageable);
        Type listType = new TypeToken<List<CronJobDto>>() {
        }.getType();
        cronJobWsDto.setCronJobs(modelMapper.map(page.getContent(), listType));
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        cronJobWsDto.setTotalPages(page.getTotalPages());
        cronJobWsDto.setTotalRecords(page.getTotalElements());
        cronJobWsDto.setAttributeList(getConfiguredAttributes(cronJobWsDto.getNode()));
        cronJobWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.CRONJOB));
        return cronJobWsDto;
    }

    @GetMapping("/get")
    public CronJobWsDto getAllActiveCronJobs() {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        Type listType = new TypeToken<List<CronJobDto>>() {
        }.getType();
        cronJobWsDto.setCronJobs(modelMapper.map(cronRepository.findByStatusOrderByIdentifier(true), listType));
        return cronJobWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new CronJob());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.CRONJOB);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CronJobDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(cronRepository.findByIdentifier(recordId), CronJobDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public CronJobWsDto saveCronJobs(@RequestBody CronJobWsDto request) {
        return cronJobService.handleEdit(request);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public CronJobWsDto edits(@RequestBody CronJobWsDto request) {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        List<CronJob> cronJobs = new ArrayList<>();
        for (CronJobDto cronJobDto : request.getCronJobs()) {
            cronJobs.add(cronRepository.findByIdentifier(cronJobDto.getIdentifier()));
        }
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        Type listType = new TypeToken<List<CronJobDto>>() {
        }.getType();
        cronJobWsDto.setCronJobs(modelMapper.map(cronJobs, listType));
        cronJobWsDto.setRedirectUrl("");
        return cronJobWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CronJobWsDto deleteCronJob(@RequestBody CronJobWsDto cronJobWsDto) {
        for (CronJobDto cronJobDto : cronJobWsDto.getCronJobs()) {
            cronJobService.stopCronJob(cronJobDto.getIdentifier());
            cronRepository.deleteByIdentifier(cronJobDto.getIdentifier());
        }
        cronJobWsDto.setMessage("Data deleted successfully");
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        return cronJobWsDto;
    }

    @GetMapping("/copy")
    @ResponseBody
    public CronJobWsDto copy(@RequestParam("recordId") String recordId) {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        CronJob cronJob = cronRepository.findByIdentifier(recordId);
        CronJobDto cronJobDto = modelMapper.map(cronJob, CronJobDto.class);
        cronJobDto.setIdentifier(null);
        cronJobDto.setCreationTime(new Date());
        cronJobDto.setLastModified(new Date());
        CronJob clonedCronjob = modelMapper.map(cronJobDto, CronJob.class);
        cronRepository.save(clonedCronjob);
        String id = String.valueOf(clonedCronjob.getId().getTimestamp());
        if (cronRepository.findByIdentifier(id) != null) {
            id = id + new Random().nextInt(24565);
        }
        clonedCronjob.setIdentifier(id);
        clonedCronjob.setIdentifier("Copy_" + id + cronJob.getIdentifier());
        cronRepository.save(clonedCronjob);
        cronJobWsDto.setMessage("Cronjob cloned successfully!!");
        return cronJobWsDto;
    }

    @PostMapping("/startJob")
    @ResponseBody
    public CronJobWsDto startJob(@RequestBody CronJobWsDto cronJobWsDto) {
        for (CronJobDto cronJobDto : cronJobWsDto.getCronJobs()) {
            cronJobService.startCronJob(cronJobDto.getIdentifier(), qualityAssuranceService);
        }
        cronJobWsDto.setMessage("Cronjob started successfully!!");
        return cronJobWsDto;
    }

    @PostMapping("/stopJob")
    @ResponseBody
    public CronJobWsDto stopJob(@RequestBody CronJobWsDto cronJobWsDto) {
        for (CronJobDto cronJobDto : cronJobWsDto.getCronJobs()) {
            cronJobService.stopCronJob(cronJobDto.getIdentifier());
        }
        cronJobWsDto.setMessage("Cronjob stopped successfully!!");
        return cronJobWsDto;
    }

    @PostMapping("/run")
    @ResponseBody
    public String runCronJob(@RequestBody CronJobDto cronJobDto) {
        CronJob cronJob = cronRepository.findByIdentifier(cronJobDto.getIdentifier());
        Map<String, String> data = commonService.toMap(cronJob);
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();
        data.put(TestDataUtils.Field.SESSION_ID.toString(), sessionId);
        data.put(TestDataUtils.Field.JOB_TIME.toString(), df.format(new Date()));
        qualityAssuranceService.processData(data);
        return "Success";
    }

    @PostMapping("/upload")
    @ResponseBody
    public CronJobWsDto uploadFile(@RequestBody MultipartFile file) {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.CRONJOB, EntityConstants.CRONJOB_MODEL, cronJobWsDto);
            if (StringUtils.isEmpty(cronJobWsDto.getMessage())) {
                cronJobWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return cronJobWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public CronJobWsDto uploadFile(@RequestBody CronJobWsDto cronJobWsDto) {

        try {
            cronJobWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CRONJOB, cronJobWsDto.getHeaderFields()));
            return cronJobWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
