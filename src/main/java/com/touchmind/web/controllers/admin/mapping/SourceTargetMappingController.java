package com.touchmind.web.controllers.admin.mapping;

import com.touchmind.core.mongo.dto.DataSourceDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SourceTargetMappingDto;
import com.touchmind.core.mongo.dto.SourceTargetMappingWsDto;
import com.touchmind.core.mongo.model.DataRelation;
import com.touchmind.core.mongo.model.DataRelationParams;
import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.repository.DataRelationRepository;
import com.touchmind.core.mongo.repository.DataSourceRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.core.service.SourceTargetMappingService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/mapping")
public class SourceTargetMappingController extends BaseController {

    public static final String ADMIN_MAPPING = "/admin/mapping";
    Logger logger = LoggerFactory.getLogger(SourceTargetMappingController.class);
    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataRelationRepository dataRelationRepository;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private SourceTargetMappingService sourceTargetMappingService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public SourceTargetMappingWsDto getAllModels(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {
        Pageable pageable = getPageable(sourceTargetMappingWsDto.getPage(), sourceTargetMappingWsDto.getSizePerPage(), sourceTargetMappingWsDto.getSortDirection(), sourceTargetMappingWsDto.getSortField());
        SourceTargetMappingDto sourceTargetMappingDto = CollectionUtils.isNotEmpty(sourceTargetMappingWsDto.getSourceTargetMappings()) ? sourceTargetMappingWsDto.getSourceTargetMappings().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(sourceTargetMappingDto, sourceTargetMappingWsDto.getOperator());
        SourceTargetMapping sourceTargetMapping = sourceTargetMappingDto != null ? modelMapper.map(sourceTargetMappingDto, SourceTargetMapping.class) : null;
        Page<SourceTargetMapping> page = isSearchActive(sourceTargetMapping) != null ? sourceTargetMappingRepository.findAll(Example.of(sourceTargetMapping, exampleMatcher), pageable) : sourceTargetMappingRepository.findAll(pageable);
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(page.getContent(), List.class));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        sourceTargetMappingWsDto.setTotalPages(page.getTotalPages());
        sourceTargetMappingWsDto.setTotalRecords(page.getTotalElements());
        sourceTargetMappingWsDto.setAttributeList(getConfiguredAttributes(sourceTargetMappingWsDto.getNode()));
        return sourceTargetMappingWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new SourceTargetMapping());
    }

    @GetMapping("/get")
    public SourceTargetMappingWsDto getActiveSourceTargetMapping() {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappingRepository.findByStatusOrderByIdentifier(true), List.class));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public SourceTargetMappingWsDto handleEdit(@RequestBody SourceTargetMappingWsDto request) {
        return sourceTargetMappingService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SourceTargetMappingWsDto addDataSource() {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SourceTargetMappingWsDto edits(@RequestBody SourceTargetMappingWsDto request) {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        List<SourceTargetMapping> sourceTargetMappings = new ArrayList<>();
        for (SourceTargetMappingDto sourceTargetMappingDto : request.getSourceTargetMappings()) {
            sourceTargetMappings.add(sourceTargetMappingRepository.findByRecordId(sourceTargetMappingDto.getRecordId()));
        }
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappings, List.class));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        sourceTargetMappingWsDto.setRedirectUrl("");
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public SourceTargetMappingWsDto deleteDataSource(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {
        for (SourceTargetMappingDto sourceTargetMappingDto : sourceTargetMappingWsDto.getSourceTargetMappings()) {
            sourceTargetMappingRepository.deleteByRecordId(sourceTargetMappingDto.getRecordId());
        }
        sourceTargetMappingWsDto.setMessage("Data deleted successfully!!");
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/getDataSourcesByRelationId")
    @ResponseBody
    public List<DataSourceDto> getDataSourcesByRelationId(@RequestBody SourceTargetMappingDto sourceTargetMappingDto) {
        DataRelation dataRelation = dataRelationRepository.findByRecordId(sourceTargetMappingDto.getRecordId());
        List<DataRelationParams> dataRelationParams = dataRelation.getDataRelationParams();
        if (CollectionUtils.isNotEmpty(dataRelationParams)) {
            return modelMapper.map(getDataSourcesById(dataRelationParams), List.class);
        }
        return Collections.EMPTY_LIST;
    }

    private List<DataSource> getDataSourcesById(List<DataRelationParams> dataRelationParams) {
        List<DataSource> dataSources = new ArrayList<>();
        for (DataRelationParams dataRelationParam : dataRelationParams) {
            DataSource dataSource = dataSourceRepository.findByRecordId(dataRelationParam.getDataSource());
            if (dataSource != null) {
                dataSources.add(dataSource);
            }
        }
        return dataSources;
    }

    @PostMapping("/getDataSourceParamsById")
    @ResponseBody
    public List<String> getDataSourceParamsById(@RequestBody SourceTargetMappingDto sourceTargetMapping) {
        DataSource dataSource = dataSourceRepository.findByRecordId(sourceTargetMapping.getRecordId());
        List<String> params = new ArrayList<>();
        if (dataSource != null) {
            params.addAll(dataSource.getSrcInputParams());
            params.addAll(dataSource.getDataSourceInputs().stream().map(dataSourceInput -> dataSourceInput.getFieldName()).collect(Collectors.toList()));
        }
        return params;
    }

    @PostMapping("/upload")
    public SourceTargetMappingWsDto uploadFile(@RequestBody MultipartFile file) {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SOURCE_TARGET_MAPPING, EntityConstants.SOURCE_TARGET_MAPPING, sourceTargetMappingWsDto);
            if (StringUtils.isEmpty(sourceTargetMappingWsDto.getMessage())) {
                sourceTargetMappingWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return sourceTargetMappingWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public SourceTargetMappingWsDto uploadFile() {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        try {
            sourceTargetMappingWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SOURCE_TARGET_MAPPING));
            return sourceTargetMappingWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
