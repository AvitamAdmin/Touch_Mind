package com.touchmind.web.controllers.admin.scheduler;

import com.touchmind.core.mongo.dto.SchedulerJobDto;
import com.touchmind.core.mongo.dto.SchedulerJobWsDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.SchedulerJob;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SchedulerJobRepository;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.core.service.CommonService;
import com.touchmind.core.service.SchedulerJobService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.form.SchedulerForm;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.tookit.service.ReportService;
import com.touchmind.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    @PostMapping
    @ResponseBody
    public SchedulerJobWsDto getAllCronJobs(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        Pageable pageable = getPageable(schedulerJobWsDto.getPage(), schedulerJobWsDto.getSizePerPage(), schedulerJobWsDto.getSortDirection(), schedulerJobWsDto.getSortField());
        SchedulerJobDto schedulerJobDto = CollectionUtils.isNotEmpty(schedulerJobWsDto.getSchedulerJobs()) ? schedulerJobWsDto.getSchedulerJobs().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(schedulerJobDto, schedulerJobWsDto.getOperator());
        SchedulerJob schedulerJob = schedulerJobDto != null ? modelMapper.map(schedulerJobDto, SchedulerJob.class) : null;
        Page<SchedulerJob> page = isSearchActive(schedulerJob) != null ? schedulerJobRepository.findAll(Example.of(schedulerJob, exampleMatcher), pageable) : schedulerJobRepository.findAll(pageable);
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(page.getContent(), List.class));
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        schedulerJobWsDto.setTotalPages(page.getTotalPages());
        schedulerJobWsDto.setTotalRecords(page.getTotalElements());
        schedulerJobWsDto.setAttributeList(getConfiguredAttributes(schedulerJobWsDto.getNode()));
        return schedulerJobWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new SchedulerJob());
    }

    @GetMapping("/get")
    @ResponseBody
    public SchedulerJobWsDto getActiveSchedulers() {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(schedulerJobRepository.findByStatusOrderByIdentifier(true), List.class));
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        return schedulerJobWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public SchedulerJobWsDto handleEdit(@RequestBody SchedulerJobWsDto request) {
        return schedulerJobService.handleEdit(request);
    }

    @PostMapping("/run")
    @ResponseBody
    public String handleEditOne(@ModelAttribute("editForm") SchedulerForm schedulerForm, Model model, BindingResult result) {
        SchedulerJob cronJob = schedulerJobService.findByRecordId(schedulerForm.getRecordId());
        Map<String, String> data = commonService.toMap(cronJob);
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();
        data.put(TestDataUtils.Field.SESSION_ID.toString(), sessionId);
        data.put(TestDataUtils.Field.JOB_TIME.toString(), df.format(new Date()));
        reportService.processData(data);
        return "Success";
    }

    private SchedulerJob getScheduler(SchedulerForm schedulerForm, Node node) {
        SchedulerJob cronJob = StringUtils.isNotEmpty(schedulerForm.getRecordId()) ? schedulerJobService.findByRecordId(schedulerForm.getRecordId()) : modelMapper.map(schedulerForm, SchedulerJob.class);
        cronJob.setShortcuts(StringUtils.join(schedulerForm.getShortcuts(), ","));
        cronJob.setSites(StringUtils.join(schedulerForm.getSites(), ","));
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
        cronJob.setInterfaceName(String.valueOf(schedulerForm.getId()));
        return cronJob;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SchedulerJobWsDto editMultiple(@RequestBody SchedulerJobWsDto request) {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        List<SchedulerJob> schedulerJobs = new ArrayList<>();
        for (SchedulerJobDto schedulerJobDto : request.getSchedulerJobs()) {
            schedulerJobs.add(schedulerJobRepository.findByRecordId(schedulerJobDto.getRecordId()));
        }
        schedulerJobWsDto.setSchedulerJobs(modelMapper.map(schedulerJobs, List.class));
        schedulerJobWsDto.setRedirectUrl("/admin/role");
        schedulerJobWsDto.setBaseUrl(ADMIN_SCHEDULER);
        return schedulerJobWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public SchedulerJobWsDto deleteScheduler(@RequestBody SchedulerJobWsDto schedulerJobWsDto) {
        for (SchedulerJobDto schedulerJobDto : schedulerJobWsDto.getSchedulerJobs()) {
            schedulerJobRepository.deleteByRecordId(schedulerJobDto.getRecordId());
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

    @GetMapping("/startJob/{id}")
    @ResponseBody
    public String startJob(@PathVariable(required = true) String id) {
        schedulerJobService.startCronJob(id);
        return "redirect:/admin/scheduler";
    }

    @GetMapping("/stopJob")
    @ResponseBody
    public String stopJob(@RequestParam("id") String id) {
        schedulerJobService.stopCronJob(id);
        return "redirect:/admin/scheduler";
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

    @GetMapping("/export")
    @ResponseBody
    public SchedulerJobWsDto uploadFile() {
        SchedulerJobWsDto schedulerJobWsDto = new SchedulerJobWsDto();
        try {
            schedulerJobWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SCHEDULER));
            return schedulerJobWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
