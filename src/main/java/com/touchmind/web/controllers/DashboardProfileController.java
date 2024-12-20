package com.touchmind.web.controllers;

import com.touchmind.core.mongo.dto.DashboardProfileDto;
import com.touchmind.core.mongo.dto.DashboardProfileWsDto;
import com.touchmind.core.mongo.model.DashboardProfile;
import com.touchmind.core.mongo.repository.DashboardProfileRepository;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.DashboardProfileService;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
//    @Autowired
//    private FileImportService fileImportService;
//    @Autowired
//    private FileExportService fileExportService;
    @Autowired
    private ModelMapper modelMapper;


    @PostMapping
    @ResponseBody
    public DashboardProfileWsDto getDashboardProfile(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {
        Pageable pageable = getPageable(dashboardProfileWsDto.getPage(), dashboardProfileWsDto.getSizePerPage(), dashboardProfileWsDto.getSortDirection(), dashboardProfileWsDto.getSortField());
        DashboardProfileDto dashboardProfileDto = CollectionUtils.isNotEmpty(dashboardProfileWsDto.getDashboardProfiles()) ? dashboardProfileWsDto.getDashboardProfiles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dashboardProfileDto, dashboardProfileWsDto.getOperator());
        DashboardProfile dashboardProfile = dashboardProfileDto != null ? modelMapper.map(dashboardProfileDto, DashboardProfile.class) : null;
        Page<DashboardProfile> page = isSearchActive(dashboardProfile) != null ? dashboardProfileRepository.findAll(Example.of(dashboardProfile, exampleMatcher), pageable) : dashboardProfileRepository.findAll(pageable);
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(page.getContent(), List.class));
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setTotalPages(page.getTotalPages());
        dashboardProfileWsDto.setTotalRecords(page.getTotalElements());
       // dashboardProfileWsDto.setAttributeList(getConfiguredAttributes(dashboardProfileWsDto.getNode()));
        return dashboardProfileWsDto;
    }

//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new DashboardProfile());
//    }


    @GetMapping("/get")
    public DashboardProfileWsDto getActiveDashboardProfiles() {
        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfileRepository.findByStatusOrderByIdentifier(true), List.class));
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
            dashboardProfiles.add(dashboardProfileRepository.findByRecordId(dashboardProfileDto.getRecordId()));
        }
        dashboardProfileWsDto.setDashboardProfiles(modelMapper.map(dashboardProfiles, List.class));
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        return dashboardProfileWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public DashboardProfileWsDto deleteDashboardProfile(@RequestBody DashboardProfileWsDto dashboardProfileWsDto) {
        for (DashboardProfileDto dashboardProfileDto : dashboardProfileWsDto.getDashboardProfiles()) {
            dashboardProfileService.deleteDashboardProfile(dashboardProfileDto.getRecordId());
        }
        dashboardProfileWsDto.setBaseUrl(ADMIN_DASHBOARDPROFILE);
        dashboardProfileWsDto.setMessage("Data was deleted successfully!!");
        return dashboardProfileWsDto;
    }

//    @PostMapping("/upload")
//    @ResponseBody
//    public DashboardProfileWsDto uploadFile(@RequestBody MultipartFile file) {
//        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DASHBOARD_PROFILE, EntityConstants.DASHBOARD_PROFILE, dashboardProfileWsDto);
//            if (StringUtils.isEmpty(dashboardProfileWsDto.getMessage())) {
//                dashboardProfileWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return dashboardProfileWsDto;
//    }
//
//    @GetMapping("/export")
//    @ResponseBody
//    public DashboardProfileWsDto uploadFile() {
//        DashboardProfileWsDto dashboardProfileWsDto = new DashboardProfileWsDto();
//        try {
//            dashboardProfileWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DASHBOARD_PROFILE));
//            return dashboardProfileWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
}
