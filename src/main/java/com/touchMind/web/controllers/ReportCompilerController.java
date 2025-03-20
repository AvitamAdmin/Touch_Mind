package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.ReportCompilerDto;
import com.touchMind.core.mongo.dto.ReportCompilerWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.ReportCompiler;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.ReportCompilerRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.ReportCompilerService;
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
@RequestMapping("/admin/reportCompiler")
public class ReportCompilerController extends BaseController {

    public static final String ADMIN_REPORTCOMPILER = "/admin/reportCompiler";

    Logger logger = LoggerFactory.getLogger(ReportCompilerController.class);
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private ReportCompilerRepository reportCompilerRepository;
    @Autowired
    private DataRelationRepository dataRelationRepository;
    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private ReportCompilerService reportCompilerService;
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
    public ReportCompilerWsDto getAllData(@RequestBody ReportCompilerWsDto reportCompilerWsDto) {
        Pageable pageable = getPageable(reportCompilerWsDto.getPage(), reportCompilerWsDto.getSizePerPage(), reportCompilerWsDto.getSortDirection(), reportCompilerWsDto.getSortField());
        ReportCompilerDto reportCompilerDto = CollectionUtils.isNotEmpty(reportCompilerWsDto.getReportCompilers()) ? reportCompilerWsDto.getReportCompilers().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(reportCompilerDto, reportCompilerWsDto.getOperator());
        ReportCompiler reportCompiler = reportCompilerDto != null ? modelMapper.map(reportCompilerDto, ReportCompiler.class) : null;
        Page<ReportCompiler> page = isSearchActive(reportCompiler) != null ? reportCompilerRepository.findAll(Example.of(reportCompiler, exampleMatcher), pageable) : reportCompilerRepository.findAll(pageable);
        Type listType = new TypeToken<List<ReportCompilerDto>>() {
        }.getType();
        reportCompilerWsDto.setReportCompilers(modelMapper.map(page.getContent(), listType));
        reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
        reportCompilerWsDto.setTotalPages(page.getTotalPages());
        reportCompilerWsDto.setTotalRecords(page.getTotalElements());
        reportCompilerWsDto.setAttributeList(getConfiguredAttributes(reportCompilerWsDto.getNode()));
        reportCompilerWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.REPORT_COMPILER));
        return reportCompilerWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody ReportCompilerWsDto reportCompilerWsDto) {
        return getConfiguredAttributes(reportCompilerWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new ReportCompiler());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.REPORT_COMPILER);
    }

    @GetMapping("/get")
    @ResponseBody
    public ReportCompilerWsDto getActiveReports() {
        ReportCompilerWsDto reportCompilerWsDto = new ReportCompilerWsDto();
        reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
        Type listType = new TypeToken<List<ReportCompilerDto>>() {
        }.getType();
        reportCompilerWsDto.setReportCompilers(modelMapper.map(reportCompilerRepository.findAllByOrderByIdentifier(), listType));
        return reportCompilerWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public ReportCompilerWsDto handleEdit(@RequestBody ReportCompilerWsDto request) {
        return reportCompilerService.handleEdit(request);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody ReportCompilerDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(reportCompilerRepository.findByIdentifier(recordId), ReportCompilerDto.class);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ReportCompilerWsDto editMultiple(@RequestBody ReportCompilerWsDto request) {
        ReportCompilerWsDto reportCompilerWsDto = new ReportCompilerWsDto();
        List<ReportCompiler> reportCompilers = new ArrayList<>();
        for (ReportCompilerDto reportCompilerDto : request.getReportCompilers()) {
            reportCompilers.add(reportCompilerRepository.findByIdentifier(reportCompilerDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<ReportCompilerDto>>() {
        }.getType();
        reportCompilerWsDto.setReportCompilers(modelMapper.map(reportCompilers, listType));
        reportCompilerWsDto.setRedirectUrl("");
        reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
        return reportCompilerWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public ReportCompilerWsDto addDashboard() {
        ReportCompilerWsDto reportCompilerWsDto = new ReportCompilerWsDto();
        reportCompilerWsDto.setExistingParamsCount(0);
        reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
        return reportCompilerWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public ReportCompilerWsDto deleteCategory(@RequestBody ReportCompilerWsDto reportCompilerWsDto) {
        for (ReportCompilerDto reportCompilerDto : reportCompilerWsDto.getReportCompilers()) {
            reportCompilerRepository.deleteByIdentifier(reportCompilerDto.getIdentifier());
        }
        reportCompilerWsDto.setBaseUrl(ADMIN_REPORTCOMPILER);
        reportCompilerWsDto.setMessage("Data deleted successfully!!");
        return reportCompilerWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public ReportCompilerWsDto uploadFile(@RequestBody ReportCompilerWsDto reportCompilerWsDto) {

        try {
            reportCompilerWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.REPORT_COMPILER, reportCompilerWsDto.getHeaderFields()));
            return reportCompilerWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    public ReportCompilerWsDto uploadFile(@RequestBody MultipartFile file) {
        ReportCompilerWsDto reportCompilerWsDto = new ReportCompilerWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.REPORT_COMPILER, EntityConstants.REPORT_COMPILER, reportCompilerWsDto);
            if (StringUtils.isEmpty(reportCompilerWsDto.getMessage())) {
                reportCompilerWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return reportCompilerWsDto;
    }
}
