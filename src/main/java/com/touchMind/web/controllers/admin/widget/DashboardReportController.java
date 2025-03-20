package com.touchMind.web.controllers.admin.widget;

import com.touchMind.core.mongo.dto.DashboardReportDto;
import com.touchMind.core.mongo.dto.DashboardReportWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.DashboardReport;
import com.touchMind.core.mongo.repository.DashboardReportRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboardReport")
public class DashboardReportController extends BaseController {

    public static final String ADMIN_WIDGET = "/admin/dashboardReport";

    Logger logger = LoggerFactory.getLogger(DashboardReportController.class);

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Autowired
    private DashboardReportRepository dashboardReportRepository;

    @PostMapping
    @ResponseBody
    public DashboardReportWsDto getAll(@RequestBody DashboardReportWsDto dashboardReportWsDto) {
        Pageable pageable = getPageable(dashboardReportWsDto.getPage(), dashboardReportWsDto.getSizePerPage(), dashboardReportWsDto.getSortDirection(), dashboardReportWsDto.getSortField());
        DashboardReportDto dashboardReportDto = CollectionUtils.isNotEmpty(dashboardReportWsDto.getDashboardReports()) ? dashboardReportWsDto.getDashboardReports().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dashboardReportDto, dashboardReportWsDto.getOperator());
        DashboardReport dashboardReport = dashboardReportDto != null ? modelMapper.map(dashboardReportDto, DashboardReport.class) : null;
        Page<DashboardReport> page = isSearchActive(dashboardReport) != null ? dashboardReportRepository.findAll(Example.of(dashboardReport, exampleMatcher), pageable) : dashboardReportRepository.findAll(pageable);
        Type listType = new TypeToken<List<DashboardReportDto>>() {
        }.getType();
        dashboardReportWsDto.setDashboardReports(modelMapper.map(page.getContent(), listType));
        dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        dashboardReportWsDto.setTotalPages(page.getTotalPages());
        dashboardReportWsDto.setTotalRecords(page.getTotalElements());
        dashboardReportWsDto.setAttributeList(getConfiguredAttributes(dashboardReportWsDto.getNode()));
        dashboardReportWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DASHBOARD_REPORT));
        return dashboardReportWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody DashboardReportWsDto dashboardReportWsDto) {
        return getConfiguredAttributes(dashboardReportWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new DashboardReport());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.DASHBOARD_REPORT);
    }

    @GetMapping("/get")
    @ResponseBody
    public DashboardReportWsDto getActiveData() {
        DashboardReportWsDto dashboardReportWsDto = new DashboardReportWsDto();
        Type listType = new TypeToken<List<DashboardReportDto>>() {
        }.getType();
        dashboardReportWsDto.setDashboardReports(modelMapper.map(dashboardReportRepository.findByStatusOrderByIdentifier(true), listType));
        dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        return dashboardReportWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody DashboardReportDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(dashboardReportRepository.findByIdentifier(recordId), DashboardReportDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public DashboardReportWsDto handleEdit(@RequestBody DashboardReportWsDto request) {
        DashboardReportWsDto dashboardReportWsDto = new DashboardReportWsDto();
        List<DashboardReportDto> dashboardReports = request.getDashboardReports();
        List<DashboardReport> displayTypes = new ArrayList<>();
        DashboardReport requestData = null;
        for (DashboardReportDto dashboardReportDto : dashboardReports) {
            if (dashboardReportDto.isAdd() && baseService.validateIdentifier(EntityConstants.DASHBOARD_REPORT, dashboardReportDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            if (dashboardReportDto.getIdentifier() != null) {
                requestData = dashboardReportRepository.findByIdentifier(dashboardReportDto.getIdentifier());
                modelMapper.map(dashboardReportDto, requestData);
            } else {

                requestData = modelMapper.map(dashboardReportDto, DashboardReport.class);
            }
            baseService.populateCommonData(requestData);
            dashboardReportRepository.save(requestData);
            displayTypes.add(requestData);
            dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        }
        Type listType = new TypeToken<List<DashboardReportDto>>() {
        }.getType();
        dashboardReportWsDto.setDashboardReports(modelMapper.map(displayTypes, listType));
        dashboardReportWsDto.setMessage("Dashboard report updated successfully!!");
        return dashboardReportWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public DashboardReportWsDto add() {
        DashboardReportWsDto dashboardReportWsDto = new DashboardReportWsDto();
        dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        return dashboardReportWsDto;
    }

    @PostMapping("/delete")
    public DashboardReportWsDto delete(@RequestBody DashboardReportWsDto dashboardReportWsDto) {
        for (DashboardReportDto dashboardReportDto : dashboardReportWsDto.getDashboardReports()) {
            dashboardReportRepository.deleteByIdentifier(dashboardReportDto.getIdentifier());
        }
        dashboardReportWsDto.setMessage("Data deleted Successfully!!");
        dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        return dashboardReportWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public DashboardReportWsDto editMultiple(@RequestBody DashboardReportWsDto request) {
        DashboardReportWsDto dashboardReportWsDto = new DashboardReportWsDto();
        List<DashboardReport> dashboardReports = new ArrayList<>();
        for (DashboardReportDto dashboardReportDto : request.getDashboardReports()) {
            dashboardReports.add(dashboardReportRepository.findByIdentifier(dashboardReportDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<DashboardReportDto>>() {
        }.getType();
        dashboardReportWsDto.setDashboardReports(modelMapper.map(dashboardReports, listType));
        dashboardReportWsDto.setRedirectUrl(ADMIN_WIDGET);
        dashboardReportWsDto.setBaseUrl(ADMIN_WIDGET);
        return dashboardReportWsDto;
    }

    @PostMapping("/upload")
    public DashboardReportWsDto uploadFile(@RequestBody MultipartFile file) {
        DashboardReportWsDto dashboardReportWsDto = new DashboardReportWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DASHBOARD_REPORT, EntityConstants.DASHBOARD_REPORT, dashboardReportWsDto);
            if (StringUtils.isEmpty(dashboardReportWsDto.getMessage())) {
                dashboardReportWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return dashboardReportWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public DashboardReportWsDto uploadFile(@RequestBody DashboardReportWsDto dashboardReportWsDto) {

        try {
            dashboardReportWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DASHBOARD_REPORT, dashboardReportWsDto.getHeaderFields()));
            return dashboardReportWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
