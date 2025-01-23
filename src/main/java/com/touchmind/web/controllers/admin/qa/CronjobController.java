package com.touchmind.web.controllers.admin.qa;

import com.touchmind.core.mongo.dto.CronJobDto;
import com.touchmind.core.mongo.dto.CronJobWsDto;
import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.CronJob;
import com.touchmind.core.mongo.repository.*;
import com.touchmind.core.service.*;
import com.touchmind.core.service.cronjob.CronJobService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.web.controllers.BaseController;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private TestProfileRepository testProfileRepository;
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

    @PostMapping
    @ResponseBody
    public CronJobWsDto getAllCronJobs(@RequestBody CronJobWsDto cronJobWsDto) {
        Pageable pageable = getPageable(cronJobWsDto.getPage(), cronJobWsDto.getSizePerPage(), cronJobWsDto.getSortDirection(), cronJobWsDto.getSortField());
        CronJobDto cronJobDto = CollectionUtils.isNotEmpty(cronJobWsDto.getCronJobs()) ? cronJobWsDto.getCronJobs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(cronJobDto, cronJobWsDto.getOperator());
        CronJob cronJob = cronJobDto != null ? modelMapper.map(cronJobDto, CronJob.class) : null;
        Page<CronJob> page = isSearchActive(cronJob) != null ? cronRepository.findAll(Example.of(cronJob, exampleMatcher), pageable) : cronRepository.findAll(pageable);
        cronJobWsDto.setCronJobs(modelMapper.map(page.getContent(), List.class));
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
        cronJobWsDto.setCronJobs(modelMapper.map(cronRepository.findByStatusOrderByIdentifier(true), List.class));
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

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody CronJobDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(cronRepository.findByRecordId(recordId), CronJobDto.class);
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
            cronJobs.add(cronRepository.findByRecordId(cronJobDto.getRecordId()));
        }
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        cronJobWsDto.setCronJobs(modelMapper.map(cronJobs, List.class));
        cronJobWsDto.setRedirectUrl("");
        return cronJobWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CronJobWsDto deleteCronJob(@RequestBody CronJobWsDto cronJobWsDto) {
        for (CronJobDto cronJobDto : cronJobWsDto.getCronJobs()) {
            cronRepository.deleteByRecordId(cronJobDto.getRecordId());
        }
        cronJobWsDto.setMessage("Data deleted successfully");
        cronJobWsDto.setBaseUrl(ADMIN_QA_CRONJOB);
        return cronJobWsDto;
    }

    @GetMapping("/startJob")
    @ResponseBody
    public CronJobWsDto startJob(CronJobWsDto cronJobWsDto) {
        cronJobService.startCronJob(cronJobWsDto.getCronJobs().get(0).getRecordId(), qualityAssuranceService);
        return cronJobWsDto;
    }

    @GetMapping("/stopJob")
    @ResponseBody
    public CronJobWsDto stopJob(CronJobWsDto cronJobWsDto) {
        for (CronJobDto cronJobDto : cronJobWsDto.getCronJobs()) {
            cronJobService.stopCronJob(cronJobDto.getRecordId());
        }
        return cronJobWsDto;
    }

    @PostMapping("/run")
    @ResponseBody
    public String runCronJob(HttpServletRequest httpRequest, @Validated @ModelAttribute("editForm") CronJobDto cronForm, Model model, BindingResult result) {
        CronJob cronJob = cronRepository.findByRecordId(cronForm.getRecordId());
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

    @GetMapping("/export")
    @ResponseBody
    public CronJobWsDto uploadFile() {
        CronJobWsDto cronJobWsDto = new CronJobWsDto();
        try {
            cronJobWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CRONJOB));
            return cronJobWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
