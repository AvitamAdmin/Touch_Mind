package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.DashboardDto;
import com.touchMind.core.mongo.dto.DashboardWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Dashboard;
import com.touchMind.core.mongo.repository.DashboardRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.DashboardService;
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
@RequestMapping("/admin/dashboard")
public class DashboardManagerController extends BaseController {

    public static final String ADMIN_DASHBOARD = "/admin/dashboard";
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public DashboardWsDto getDashBoardData(@RequestBody DashboardWsDto dashboardWsDto) {
        Pageable pageable = getPageable(dashboardWsDto.getPage(), dashboardWsDto.getSizePerPage(), dashboardWsDto.getSortDirection(), dashboardWsDto.getSortField());
        DashboardDto dashboardDto = CollectionUtils.isNotEmpty(dashboardWsDto.getDashboards()) ? dashboardWsDto.getDashboards().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dashboardDto, dashboardWsDto.getOperator());
        Dashboard dashboard = dashboardDto != null ? modelMapper.map(dashboardDto, Dashboard.class) : null;
        Page<Dashboard> page = isSearchActive(dashboard) != null ? dashboardRepository.findAll(Example.of(dashboard, exampleMatcher), pageable) : dashboardRepository.findAll(pageable);
        Type listType = new TypeToken<List<DashboardDto>>() {
        }.getType();
        dashboardWsDto.setDashboards(modelMapper.map(page.getContent(), listType));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setTotalPages(page.getTotalPages());
        dashboardWsDto.setTotalRecords(page.getTotalElements());
        dashboardWsDto.setAttributeList(getConfiguredAttributes(dashboardWsDto.getNode()));
        dashboardWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DASHBOARD));
        return dashboardWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody DashboardWsDto dashboardWsDto) {
        return getConfiguredAttributes(dashboardWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Dashboard());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.DASHBOARD);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody DashboardDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(dashboardRepository.findByIdentifier(recordId), DashboardDto.class);
    }

    @GetMapping("/get")
    @ResponseBody
    public DashboardWsDto getActiveDashboards() {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        Type listType = new TypeToken<List<DashboardDto>>() {
        }.getType();
        dashboardWsDto.setDashboards(modelMapper.map(dashboardRepository.findByStatusOrderByIdentifier(true), listType));
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
        Type listType = new TypeToken<List<DashboardDto>>() {
        }.getType();
        dashboardWsDto.setDashboards(modelMapper.map(dashboardRepository.findByStatusOrderByIdentifier(true), listType));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        return dashboardWsDto;
    }


    @PostMapping("/getedits")
    @ResponseBody
    public DashboardWsDto editMultiple(@RequestBody DashboardWsDto request) {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        List<Dashboard> dashboards = new ArrayList<>();
        for (DashboardDto dashboardDto : request.getDashboards()) {
            dashboards.add(dashboardRepository.findByIdentifier(dashboardDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<DashboardDto>>() {
        }.getType();
        dashboardWsDto.setDashboards(modelMapper.map(dashboards, listType));
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        return dashboardWsDto;
    }


    @PostMapping("/delete")
    @ResponseBody
    public DashboardWsDto deleteCategory(@RequestBody DashboardWsDto dashboardWsDto) {
        for (DashboardDto dashboardDto : dashboardWsDto.getDashboards()) {
            dashboardRepository.deleteByIdentifier(dashboardDto.getIdentifier());
        }
        dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
        dashboardWsDto.setMessage("Data deleted successfully!!");
        return dashboardWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public DashboardWsDto uploadFile(@RequestBody DashboardWsDto dashboardWsDto) {

        try {
            dashboardWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DASHBOARD, dashboardWsDto.getHeaderFields()));
            return dashboardWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    public DashboardWsDto uploadFile(@RequestBody MultipartFile file) {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DASHBOARD, EntityConstants.DASHBOARD, dashboardWsDto);
            if (StringUtils.isEmpty(dashboardWsDto.getMessage())) {
                dashboardWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return dashboardWsDto;
    }


}
