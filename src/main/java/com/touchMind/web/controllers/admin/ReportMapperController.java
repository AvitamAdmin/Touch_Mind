package com.touchMind.web.controllers.admin;

import com.touchMind.core.mongo.dto.ReportsMapperDto;
import com.touchMind.core.mongo.dto.ReportsMapperWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.ReportsMapper;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ReportsMapperRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.ReportsMapperService;
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
@RequestMapping("/admin/reportMapper")
public class ReportMapperController extends BaseController {


    public static final String ADMIN_REPORT_MAPPER = "/admin/reportMapper";
    Logger logger = LoggerFactory.getLogger(ReportMapperController.class);

    @Autowired
    private ReportsMapperRepository reportsMapperRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private ReportsMapperService reportsMapperService;

    @PostMapping
    @ResponseBody
    public ReportsMapperWsDto getReportMapper(@RequestBody ReportsMapperWsDto reportMapperWsDto) {
        Pageable pageable = getPageable(reportMapperWsDto.getPage(), reportMapperWsDto.getSizePerPage(), reportMapperWsDto.getSortDirection(), reportMapperWsDto.getSortField());
        ReportsMapperDto reportsMapperDto = CollectionUtils.isNotEmpty(reportMapperWsDto.getReportsMapperDtoList()) ? reportMapperWsDto.getReportsMapperDtoList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(reportsMapperDto, reportMapperWsDto.getOperator());
        ReportsMapper reportsMapper = reportMapperWsDto != null ? modelMapper.map(reportMapperWsDto, ReportsMapper.class) : null;
        Page<ReportsMapper> page = isSearchActive(reportsMapper) != null ? reportsMapperRepository.findAll(Example.of(reportsMapper, exampleMatcher), pageable) : reportsMapperRepository.findAll(pageable);
        Type listType = new TypeToken<List<ReportsMapperDto>>() {
        }.getType();
        reportMapperWsDto.setReportsMapperDtoList(modelMapper.map(page.getContent(), listType));
        reportMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        reportMapperWsDto.setTotalPages(page.getTotalPages());
        reportMapperWsDto.setTotalRecords(page.getTotalElements());
        reportMapperWsDto.setAttributeList(getConfiguredAttributes(reportMapperWsDto.getNode()));
        reportMapperWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.REPORT_MAPPER));
        return reportMapperWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody ReportsMapperWsDto ReportsMapperWsDto) {
        return getConfiguredAttributes(ReportsMapperWsDto.getNode());
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new ReportsMapper());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.REPORT_MAPPER);
    }

    @GetMapping("/get")
    public ReportsMapperWsDto getActiveLocatorGroup() {
        ReportsMapperWsDto reportsMapperWsDto = new ReportsMapperWsDto();
        Type listType = new TypeToken<List<ReportsMapperDto>>() {
        }.getType();
        reportsMapperWsDto.setReportsMapperDtoList(modelMapper.map(reportsMapperRepository.findByStatusOrderByIdentifier(true), listType));
        reportsMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        return reportsMapperWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody ReportsMapperDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(reportsMapperRepository.findByIdentifier(recordId), ReportsMapperDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public ReportsMapperWsDto getLocatorForm() {
        ReportsMapperWsDto reportsMapperWsDto = new ReportsMapperWsDto();
        reportsMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        return reportsMapperWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public ReportsMapperWsDto updateReportMapper(@RequestBody ReportsMapperWsDto request) {
        return reportsMapperService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public ReportsMapperWsDto deleteReportsMapper(@RequestBody ReportsMapperWsDto reportsMapperWsDto) {
        for (ReportsMapperDto reportsMapperDto : reportsMapperWsDto.getReportsMapperDtoList()) {
            reportsMapperRepository.deleteByIdentifier(reportsMapperDto.getIdentifier());
        }
        reportsMapperWsDto.setMessage("Data deleted successfully!!");
        reportsMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        return reportsMapperWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ReportsMapperWsDto editMultiple(@RequestBody ReportsMapperWsDto request) {
        ReportsMapperWsDto reportsMapperWsDto = new ReportsMapperWsDto();
        List<ReportsMapper> reportsMappers = new ArrayList<>();
        for (ReportsMapperDto reportsMapperDto : request.getReportsMapperDtoList()) {
            reportsMappers.add(reportsMapperRepository.findByIdentifier(reportsMapperDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<ReportsMapperDto>>() {
        }.getType();
        reportsMapperWsDto.setReportsMapperDtoList(modelMapper.map(reportsMappers, listType));
        reportsMapperWsDto.setBaseUrl(ADMIN_REPORT_MAPPER);
        return reportsMapperWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ReportsMapperWsDto uploadFile(@RequestBody MultipartFile file) {
        ReportsMapperWsDto reportsMapperWsDto = new ReportsMapperWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.REPORT_MAPPER, EntityConstants.REPORT_MAPPER, reportsMapperWsDto);
            if (StringUtils.isEmpty(reportsMapperWsDto.getMessage())) {
                reportsMapperWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return reportsMapperWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public ReportsMapperWsDto uploadFile(@RequestBody ReportsMapperWsDto reportsMapperWsDto) {

        try {
            reportsMapperWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.REPORT_MAPPER, reportsMapperWsDto.getHeaderFields()));
            return reportsMapperWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


}


