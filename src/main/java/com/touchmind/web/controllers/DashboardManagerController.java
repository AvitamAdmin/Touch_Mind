package com.touchmind.web.controllers;

import com.touchmind.core.mongo.dto.DashboardDto;
import com.touchmind.core.mongo.dto.DashboardWsDto;
import com.touchmind.core.mongo.model.Dashboard;
import com.touchmind.core.mongo.repository.DashboardRepository;
import com.touchmind.core.service.DashboardService;
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
@RequestMapping("/admin/dashboard")
public class DashboardManagerController extends BaseController {

    public static final String ADMIN_DASHBOARD = "/admin/dashboard";
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private DashboardService dashboardService;


    @PostMapping
    @ResponseBody
    public DashboardWsDto getDashBoardData(@RequestBody DashboardWsDto dashboardWsDto) {
        Pageable pageable = getPageable(dashboardWsDto.getPage(), dashboardWsDto.getSizePerPage(), dashboardWsDto.getSortDirection(), dashboardWsDto.getSortField());
        DashboardDto dashboardDto = CollectionUtils.isNotEmpty(dashboardWsDto.getDashboards()) ? dashboardWsDto.getDashboards().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dashboardDto, dashboardWsDto.getOperator());
        Dashboard dashboard = dashboardDto != null ? modelMapper.map(dashboardDto, Dashboard.class) : null;
        Page<Dashboard> page = isSearchActive(dashboard) != null ? dashboardRepository.findAll(Example.of(dashboard, exampleMatcher), pageable) : dashboardRepository.findAll(pageable);
        dashboardWsDto.setDashboards(modelMapper.map(page.getContent(), List.class));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setTotalPages(page.getTotalPages());
        dashboardWsDto.setTotalRecords(page.getTotalElements());
       // dashboardWsDto.setAttributeList(getConfiguredAttributes(dashboardWsDto.getNode()));
        return dashboardWsDto;
    }

//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new Dashboard());
//    }


    @GetMapping("/get")
    @ResponseBody
    public DashboardWsDto getActiveDashboards() {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setDashboards(modelMapper.map(dashboardRepository.findByStatusOrderByIdentifier(true), List.class));
        return dashboardWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public DashboardWsDto handleEdit(@RequestBody DashboardWsDto request) {
        return dashboardService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public DashboardWsDto addDashboard() {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setDashboards(modelMapper.map(dashboardRepository.findByStatusOrderByIdentifier(true), List.class));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        return dashboardWsDto;
    }


    @PostMapping("/getedits")
    @ResponseBody
    public DashboardWsDto editMultiple(@RequestBody DashboardWsDto request) {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        List<Dashboard> dashboards = new ArrayList<>();
        for (DashboardDto dashboardDto : request.getDashboards()) {
            dashboards.add(dashboardRepository.findByRecordId(dashboardDto.getRecordId()));
        }
        dashboardWsDto.setDashboards(modelMapper.map(dashboards, List.class));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        return dashboardWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public DashboardWsDto deleteCategory(@RequestBody DashboardWsDto dashboardWsDto) {
        for (DashboardDto dashboardDto : dashboardWsDto.getDashboards()) {
            dashboardRepository.deleteByRecordId(dashboardDto.getRecordId());
        }
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setMessage("Data deleted successfully!!");
        return dashboardWsDto;
    }

//    @GetMapping("/export")
//    @ResponseBody
//    public DashboardWsDto uploadFile() {
//        DashboardWsDto dashboardWsDto = new DashboardWsDto();
//        try {
//            dashboardWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DASHBOARD));
//            return dashboardWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
//
//    @PostMapping("/upload")
//    @ResponseBody
//    public DashboardWsDto uploadFile(@RequestBody MultipartFile file) {
//        DashboardWsDto dashboardWsDto = new DashboardWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DASHBOARD, EntityConstants.DASHBOARD, dashboardWsDto);
//            if (StringUtils.isEmpty(dashboardWsDto.getMessage())) {
//                dashboardWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return dashboardWsDto;
//    }
//

}
