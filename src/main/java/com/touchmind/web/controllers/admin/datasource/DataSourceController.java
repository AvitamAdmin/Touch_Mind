package com.touchmind.web.controllers.admin.datasource;

import com.touchmind.core.mongo.dto.DataSourceDto;
import com.touchmind.core.mongo.dto.DataSourceWsDto;
import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.DataSource;
import com.touchmind.core.mongo.repository.DataSourceInputRepository;
import com.touchmind.core.mongo.repository.DataSourceRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.DataSourceService;
import com.touchmind.core.service.ServiceUtil;
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
import java.util.List;

@RestController
@RequestMapping("/admin/datasource")
public class DataSourceController extends BaseController {

    public static final String ADMIN_DATA_SOURCE = "/admin/datasource";
    public static final String DATASOURCE = "Datasource";
    Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private DataSourceInputRepository dataSourceInputRepository;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public DataSourceWsDto getAllModels(@RequestBody DataSourceWsDto dataSourceWsDto) {
        Pageable pageable = getPageable(dataSourceWsDto.getPage(), dataSourceWsDto.getSizePerPage(), dataSourceWsDto.getSortDirection(), dataSourceWsDto.getSortField());
        DataSourceDto dataSourceDto = CollectionUtils.isNotEmpty(dataSourceWsDto.getDataSources()) ? dataSourceWsDto.getDataSources().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dataSourceDto, dataSourceWsDto.getOperator());
        DataSource dataSource = dataSourceDto != null ? modelMapper.map(dataSourceDto, DataSource.class) : null;
        Page<DataSource> page = isSearchActive(dataSource) != null ? dataSourceRepository.findAll(Example.of(dataSource, exampleMatcher), pageable) : dataSourceRepository.findAll(pageable);
        dataSourceWsDto.setDataSources(modelMapper.map(page.getContent(), List.class));
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        dataSourceWsDto.setTotalPages(page.getTotalPages());
        dataSourceWsDto.setTotalRecords(page.getTotalElements());
        dataSourceWsDto.setAttributeList(getConfiguredAttributes(dataSourceWsDto.getNode()));
        dataSourceWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DATASOURCE));
        return dataSourceWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new DataSource());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.DATASOURCE);
    }

    @GetMapping("/get")
    @ResponseBody
    public DataSourceWsDto getActiveDataSources() {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        List<DataSource> models = dataSourceRepository.findByStatusOrderByIdentifier(true);
        dataSourceWsDto.setDataSources(modelMapper.map(models, List.class));
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        return dataSourceWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public DataSourceWsDto handleEdit(@RequestBody DataSourceWsDto request) {
        return dataSourceService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public DataSourceWsDto addDataSource() {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        dataSourceWsDto.setFileFormats(ServiceUtil.getSupportedFileFormats());
        dataSourceWsDto.setExistingParamsCount(0);
        List<String> inputFormats = new ArrayList<>();
        inputFormats.add("Input Box");
        inputFormats.add("Dropdown");
        inputFormats.add("Formular");
        inputFormats.add("Site Loader");
        inputFormats.add("Date and Time selector");
        dataSourceWsDto.setInputFormats(inputFormats);
        return dataSourceWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public DataSourceWsDto deleteDataSource(@RequestBody DataSourceWsDto dataSourceWsDto) {
        for (DataSourceDto dataSourceDto : dataSourceWsDto.getDataSources()) {
            dataSourceRepository.deleteByRecordId(dataSourceDto.getRecordId());
        }
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        dataSourceWsDto.setMessage("Data deleted successfully!!");
        return dataSourceWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public DataSourceWsDto edits(@RequestBody DataSourceWsDto request) {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        List<DataSource> dataSources = new ArrayList<>();
        for (DataSourceDto dataSourceDto : request.getDataSources()) {
            dataSources.add(dataSourceRepository.findByRecordId(dataSourceDto.getRecordId()));
        }
        dataSourceWsDto.setDataSources(modelMapper.map(dataSources, List.class));
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        dataSourceWsDto.setRedirectUrl("");
        return dataSourceWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public DataSourceWsDto uploadFile(@RequestBody MultipartFile file) {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DATASOURCE, EntityConstants.DATASOURCE, dataSourceWsDto);
            if (StringUtils.isEmpty(dataSourceWsDto.getMessage())) {
                dataSourceWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return dataSourceWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public DataSourceWsDto uploadFile() {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        try {
            dataSourceWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DATASOURCE));
            return dataSourceWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
