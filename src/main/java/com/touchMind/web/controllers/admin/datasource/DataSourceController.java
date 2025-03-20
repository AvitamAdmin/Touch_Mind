package com.touchMind.web.controllers.admin.datasource;

import com.touchMind.core.mongo.dto.DataSourceDto;
import com.touchMind.core.mongo.dto.DataSourceWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.repository.DataSourceInputRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.DataSourceService;
import com.touchMind.core.service.ServiceUtil;
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
        Type listType = new TypeToken<List<DataSourceDto>>() {
        }.getType();
        dataSourceWsDto.setDataSources(modelMapper.map(page.getContent(), listType));
        dataSourceWsDto.setBaseUrl(ADMIN_DATA_SOURCE);
        dataSourceWsDto.setTotalPages(page.getTotalPages());
        dataSourceWsDto.setTotalRecords(page.getTotalElements());
        dataSourceWsDto.setAttributeList(getConfiguredAttributes(dataSourceWsDto.getNode()));
        dataSourceWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DATASOURCE));
        return dataSourceWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody DataSourceWsDto dataSourceWsDto) {
        return getConfiguredAttributes(dataSourceWsDto.getNode());
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

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody DataSourceDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(dataSourceRepository.findByIdentifier(recordId), DataSourceDto.class);
    }

    @GetMapping("/get")
    @ResponseBody
    public DataSourceWsDto getActiveDataSources() {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        List<DataSource> models = dataSourceRepository.findByStatusOrderByIdentifier(true);
        Type listType = new TypeToken<List<DataSourceDto>>() {
        }.getType();
        dataSourceWsDto.setDataSources(modelMapper.map(models, listType));
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
            dataSourceRepository.deleteByIdentifier(dataSourceDto.getIdentifier());
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
            dataSources.add(dataSourceRepository.findByIdentifier(dataSourceDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<DataSourceDto>>() {
        }.getType();
        dataSourceWsDto.setDataSources(modelMapper.map(dataSources, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public DataSourceWsDto uploadFile(@RequestBody DataSourceWsDto dataSourceWsDto) {

        try {
            dataSourceWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DATASOURCE, dataSourceWsDto.getHeaderFields()));
            return dataSourceWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
