package com.touchMind.web.controllers.admin.mapping;

import com.touchMind.core.mongo.dto.DataSourceDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.SourceTargetMappingDto;
import com.touchMind.core.mongo.dto.SourceTargetMappingWsDto;
import com.touchMind.core.mongo.model.DataRelation;
import com.touchMind.core.mongo.model.DataRelationParams;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.SourceTargetMappingService;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @Autowired
    private BaseService baseService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @PostMapping
    @ResponseBody
    public SourceTargetMappingWsDto getAllModels(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {
        Pageable pageable = getPageable(sourceTargetMappingWsDto.getPage(), sourceTargetMappingWsDto.getSizePerPage(), sourceTargetMappingWsDto.getSortDirection(), sourceTargetMappingWsDto.getSortField());
        SourceTargetMappingDto sourceTargetMappingDto = CollectionUtils.isNotEmpty(sourceTargetMappingWsDto.getSourceTargetMappings()) ? sourceTargetMappingWsDto.getSourceTargetMappings().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(sourceTargetMappingDto, sourceTargetMappingWsDto.getOperator());
        SourceTargetMapping sourceTargetMapping = sourceTargetMappingDto != null ? modelMapper.map(sourceTargetMappingDto, SourceTargetMapping.class) : null;
        Page<SourceTargetMapping> page = isSearchActive(sourceTargetMapping) != null ? sourceTargetMappingRepository.findAll(Example.of(sourceTargetMapping, exampleMatcher), pageable) : sourceTargetMappingRepository.findAll(pageable);
        Type listType = new TypeToken<List<SourceTargetMappingDto>>() {
        }.getType();
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(page.getContent(), listType));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        sourceTargetMappingWsDto.setTotalPages(page.getTotalPages());
        sourceTargetMappingWsDto.setTotalRecords(page.getTotalElements());
        sourceTargetMappingWsDto.setAttributeList(getConfiguredAttributes(sourceTargetMappingWsDto.getNode()));
        sourceTargetMappingWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.SOURCE_TARGET_MAPPING));
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {
        return getConfiguredAttributes(sourceTargetMappingWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new SourceTargetMapping());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SOURCE_TARGET_MAPPING);
    }

    @GetMapping("/get")
    public SourceTargetMappingWsDto getActiveSourceTargetMapping() {
        SourceTargetMappingWsDto sourceTargetMappingWsDto = new SourceTargetMappingWsDto();
        Type listType = new TypeToken<List<SourceTargetMappingDto>>() {
        }.getType();
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappingRepository.findByStatusOrderByIdentifier(true), listType));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        return sourceTargetMappingWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody SourceTargetMappingDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(sourceTargetMappingRepository.findByIdentifier(recordId), SourceTargetMappingDto.class);
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
            sourceTargetMappings.add(sourceTargetMappingRepository.findByIdentifier(sourceTargetMappingDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<SourceTargetMappingDto>>() {
        }.getType();
        sourceTargetMappingWsDto.setSourceTargetMappings(modelMapper.map(sourceTargetMappings, listType));
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        sourceTargetMappingWsDto.setRedirectUrl("");
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public SourceTargetMappingWsDto deleteDataSource(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {
        for (SourceTargetMappingDto sourceTargetMappingDto : sourceTargetMappingWsDto.getSourceTargetMappings()) {
            sourceTargetMappingRepository.deleteByIdentifier(sourceTargetMappingDto.getIdentifier());
        }
        sourceTargetMappingWsDto.setMessage("Data deleted successfully!!");
        sourceTargetMappingWsDto.setBaseUrl(ADMIN_MAPPING);
        return sourceTargetMappingWsDto;
    }

    @PostMapping("/getDataSourcesByRelationId")
    @ResponseBody
    public List<DataSourceDto> getDataSourcesByRelationId(@RequestBody SourceTargetMappingDto sourceTargetMappingDto) {
        DataRelation dataRelation = dataRelationRepository.findByIdentifier(sourceTargetMappingDto.getIdentifier());
        if (null != dataRelation) {
            List<DataRelationParams> dataRelationParams = dataRelation.getDataRelationParams();
            if (CollectionUtils.isNotEmpty(dataRelationParams)) {
                Type listType = new TypeToken<List<SourceTargetMappingDto>>() {
                }.getType();
                return modelMapper.map(getDataSourcesById(dataRelationParams), listType);
            }
        }
        return Collections.EMPTY_LIST;
    }

    private List<DataSource> getDataSourcesById(List<DataRelationParams> dataRelationParams) {
        Set<DataSource> dataSources = new HashSet<>();
        for (DataRelationParams dataRelationParam : dataRelationParams) {
            DataSource dataSource = dataSourceRepository.findByIdentifier(dataRelationParam.getDataSource());
            if (dataSource != null) {
                dataSources.add(dataSource);
            }
        }
        return dataSources.stream().toList();
    }

    @PostMapping("/getDataSourceParamsById")
    @ResponseBody
    public List<String> getDataSourceParamsById(@RequestBody SourceTargetMappingDto sourceTargetMapping) {
        DataSource dataSource = dataSourceRepository.findByIdentifier(sourceTargetMapping.getIdentifier());
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

    @PostMapping("/export")
    @ResponseBody
    public SourceTargetMappingWsDto uploadFile(@RequestBody SourceTargetMappingWsDto sourceTargetMappingWsDto) {

        try {
            sourceTargetMappingWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SOURCE_TARGET_MAPPING, sourceTargetMappingWsDto.getHeaderFields()));
            return sourceTargetMappingWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
