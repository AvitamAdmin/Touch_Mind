package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.CronJobProfileDto;
import com.touchMind.core.mongo.dto.CronJobProfileWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.CronJobProfile;
import com.touchMind.core.mongo.repository.CronJobProfileRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CronJobProfileService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
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
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(page.getContent(), listType));
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        cronJobProfileWsDto.setTotalPages(page.getTotalPages());
        cronJobProfileWsDto.setTotalRecords(page.getTotalElements());
        cronJobProfileWsDto.setAttributeList(getConfiguredAttributes(cronJobProfileWsDto.getNode()));
        cronJobProfileWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.CRONJOB_PROFILE));
        return cronJobProfileWsDto;
    }


    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody CronJobProfileWsDto cronJobProfileWsDto) {
        return getConfiguredAttributes(cronJobProfileWsDto.getNode());
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

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CronJobProfileDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(cronJobProfileRepository.findByIdentifier(recordId), CronJobProfileDto.class);
    }

    @GetMapping("/get")
    public CronJobProfileWsDto getActiveCronJobProfiles() {
        CronJobProfileWsDto cronJobProfileWsDto = new CronJobProfileWsDto();
        cronJobProfileWsDto.setBaseUrl(ADMIN_CRONJOBPROFILE);
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), listType));
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
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), listType));
        return cronJobProfileWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public CronJobProfileWsDto deleteCategory(@RequestBody CronJobProfileWsDto cronJobProfileWsDto) {
        for (CronJobProfileDto cronJobProfileDto : cronJobProfileWsDto.getCronJobProfiles()) {
            cronJobProfileRepository.deleteByIdentifier(cronJobProfileDto.getIdentifier());
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
            cronJobProfiles.add(cronJobProfileRepository.findByIdentifier(cronJobProfileDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        cronJobProfileWsDto.setCronJobProfiles(modelMapper.map(cronJobProfiles, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public CronJobProfileWsDto uploadFile(@RequestBody CronJobProfileWsDto cronJobProfileWsDto) {

        try {
            cronJobProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.CRONJOB_PROFILE, cronJobProfileWsDto.getHeaderFields()));
            return cronJobProfileWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
