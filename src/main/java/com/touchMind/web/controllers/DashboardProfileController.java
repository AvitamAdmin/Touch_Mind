package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.DashboardProfileDto;
import com.touchMind.core.mongo.dto.DashboardProfileWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.DashboardProfile;
import com.touchMind.core.mongo.repository.DashboardProfileRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.DashboardProfileService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
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
@RequestMapping("/admin/dashboardProfile")
public class DashboardProfileController extends BaseController {

    public static final String ADMIN_DASHBOARDPROFILE = "/admin/dashboardProfile";

    @Autowired
    DashboardProfileRepository dashboardProfileRepository;
    @Autowired
    private DashboardProfileService dashboardProfileService;
    @Autowired
    private CoreService coreService;
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
    public DashboardProfileWsDto getDashboardProfile(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {
        Pageable pageable = getPageable(dashboardProfileWsDto.getPage(), dashboardProfileWsDto.getSizePerPage(), dashboardProfileWsDto.getSortDirection(), dashboardProfileWsDto.getSortField());
        DashboardProfileDto dashboardProfileDto = CollectionUtils.isNotEmpty(dashboardProfileWsDto.getDashboardProfiles()) ? dashboardProfileWsDto.getDashboardProfiles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dashboardProfileDto, dashboardProfileWsDto.getOperator());
        DashboardProfile dashboardProfile = dashboardProfileDto != null ? modelMapper.map(dashboardProfileDto, DashboardProfile.class) : null;
        Page<DashboardProfile> page = isSearchActive(dashboardProfile) != null ? dashboardProfileRepository.findAll(Example.of(dashboardProfile, exampleMatcher), pageable) : dashboardProfileRepository.findAll(pageable);
        Type listType = new TypeToken<List<DashboardProfileDto>>() {
        }.getType();
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(page.getContent(), listType));
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setTotalPages(page.getTotalPages());
        dashboardProfileWsDto.setTotalRecords(page.getTotalElements());
        dashboardProfileWsDto.setAttributeList(getConfiguredAttributes(dashboardProfileWsDto.getNode()));
        dashboardProfileWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DASHBOARD_PROFILE));
        return dashboardProfileWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {
        return getConfiguredAttributes(dashboardProfileWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new DashboardProfile());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.DASHBOARD_PROFILE);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody DashboardProfileDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(dashboardProfileRepository.findByIdentifier(recordId), DashboardProfileDto.class);
    }

    @GetMapping("/get")
    public DashboardProfileWsDto getActiveDashboardProfiles() {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        Type listType = new TypeToken<List<DashboardProfileDto>>() {
        }.getType();
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfileRepository.findByStatusOrderByIdentifier(true), listType));
        return dashboardProfileWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public DashboardProfileWsDto add() {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setDashboardLabels(dashboardProfileService.getDashboardLabels());
        return dashboardProfileWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public DashboardProfileWsDto addDashboardProfile(@RequestBody DashboardProfileWsDto request) {
        return dashboardProfileService.handleEdit(request);
    }


    @PostMapping("/getedits")
    @ResponseBody
    public DashboardProfileWsDto editMultiple(@RequestBody DashboardProfileWsDto request) {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        List<DashboardProfile> dashboardProfiles = new ArrayList<>();
        for (DashboardProfileDto dashboardProfileDto : request.getDashboardProfiles()) {
            dashboardProfiles.add(dashboardProfileRepository.findByIdentifier(dashboardProfileDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<DashboardProfileDto>>() {
        }.getType();
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfiles, listType));
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        return dashboardProfileWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public DashboardProfileWsDto deleteDashboardProfile(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {
        for (DashboardProfileDto dashboardProfileDto : dashboardProfileWsDto.getDashboardProfiles()) {
            dashboardProfileService.deleteDashboardProfile(dashboardProfileDto.getIdentifier());
        }
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setMessage("Data deleted successfully!!");
        return dashboardProfileWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public DashboardProfileWsDto uploadFile(@RequestBody MultipartFile file) {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DASHBOARD_PROFILE, EntityConstants.DASHBOARD_PROFILE, dashboardProfileWsDto);
            if (StringUtils.isEmpty(dashboardProfileWsDto.getMessage())) {
                dashboardProfileWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return dashboardProfileWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public DashboardProfileWsDto uploadFile(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {

        try {
            dashboardProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DASHBOARD_PROFILE, dashboardProfileWsDto.getHeaderFields()));
            return dashboardProfileWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
