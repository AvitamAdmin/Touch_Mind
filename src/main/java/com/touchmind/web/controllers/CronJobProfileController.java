package com.touchmind.web.controllers;

import com.touchmind.core.mongo.dto.CronJobProfileDto;
import com.touchmind.core.mongo.dto.CronJobProfileWsDto;
import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.CronJobProfile;
import com.touchmind.core.mongo.repository.CronJobProfileRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CronJobProfileService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/admin/cronjobProfile")
public class CronJobProfileController extends BaseController {

    public static final String ADMIN_CRONJOBPROFILE = "/admin/cronjobProfile";

    private static final Logger LOG = LoggerFactory.getLogger(CronJobProfileController.class);

    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;
    @Autowired
    private CronJobProfileService cronJobProfileService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;


    @PostMapping
    @ResponseBody
    public CronJobProfileWsDto getDashBoardData(@RequestBody CronJobProfileWsDto cronJobProfileWsDto) {
        Pageable pageable = getPageable(cronJobProfileWsDto.getPage(), cronJobProfileWsDto.getSizePerPage(), cronJobProfileWsDto.getSortDirection(), cronJobProfileWsDto.getSortField());
        CronJobProfileDto cronJobProfileDto = CollectionUtils.isNotEmpty(cronJobProfileWsDto.getCronJobProfiles()) ? cronJobProfileWsDto.getCronJobProfiles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(cronJobProfileDto, cronJobProfileWsDto.getOperator());
        CronJobProfile cronJobProfile = cronJobProfileDto != null ? modelMapper.map(cronJobProfileDto, CronJobProfile.class) : null;
        Page<CronJobProfile> page = isSearchActive(cronJobProfile) != null ? cronJobProfileRepository.findAll(Example.of(cronJobProfile, exampleMatcher), pageable) : cronJobProfileRepository.findAll(pageable);
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(page.getContent(), List.class));
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        cronJobProfileWsDto.setTotalPages(page.getTotalPages());
        cronJobProfileWsDto.setTotalRecords(page.getTotalElements());
        cronJobProfileWsDto.setAttributeList(getConfiguredAttributes(cronJobProfileWsDto.getNode()));
        cronJobProfileWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.CRONJOB_PROFILE));
        return cronJobProfileWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new CronJobProfile());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.CRONJOB);
    }

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody CronJobProfileDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(cronJobProfileRepository.findByRecordId(recordId), CronJobProfileDto.class);
    }

    @GetMapping("/get")
    public CronJobProfileWsDto getActiveCronJobProfiles() {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), List.class));
        return cronJobProfileWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public CronJobProfileWsDto handleEdit(@RequestBody CronJobProfileWsDto request) {
        //TODO save dynamic data
        //TODO FOR ============ Toolkit =============
        //subsidiary(String)
        //sites (List)
        // Mapping (String)
        //Cronid (String)
        //emails (List)
        //skus (List)
        //shorcuts (List)
        //TODO FOR ============ Toolkit END =============
        //TODO FOR ============ QA =============
        //emailSubject(String)
        //isDebug (Boolean)
        // dashboard (String)
        //skus (List)
        //campaign (List)
        //envProfiles (List)
        //shopCampaign (String)
        //. ............
        // {
        //  'sites':"de,se"
        //
        // }
        //TODO FOR ============ QA END =============
        return cronJobProfileService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public CronJobProfileWsDto addDashboard() {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), List.class));
        return cronJobProfileWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CronJobProfileWsDto deleteCategory(@RequestBody CronJobProfileWsDto cronJobProfileWsDto) {
        for (CronJobProfileDto cronJobProfileDto : cronJobProfileWsDto.getCronJobProfiles()) {
            cronJobProfileRepository.deleteByRecordId(cronJobProfileDto.getRecordId());
        }
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        cronJobProfileWsDto.setMessage("Data deleted successfully!!");
        return cronJobProfileWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public CronJobProfileWsDto editMultiple(@RequestBody CronJobProfileWsDto request) {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        List<CronJobProfile> cronJobProfiles = new ArrayList<>();
        for (CronJobProfileDto cronJobProfileDto : request.getCronJobProfiles()) {
            cronJobProfiles.add(cronJobProfileRepository.findByRecordId(cronJobProfileDto.getRecordId()));
        }
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfiles, List.class));
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        return cronJobProfileWsDto;
    }


    @PostMapping("/upload")
    @ResponseBody
    public CronJobProfileWsDto uploadFile(@RequestBody MultipartFile file) {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.CRONJOB_PROFILE, EntityConstants.CRONJOB_PROFILE, cronJobProfileWsDto);
            if (StringUtils.isEmpty(cronJobProfileWsDto.getMessage())) {
                cronJobProfileWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return cronJobProfileWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public CronJobProfileWsDto uploadFile() {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        try {
            cronJobProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CRONJOB_PROFILE));
            return cronJobProfileWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
