package com.touchMind.web.controllers.admin.datarelation;

import com.touchMind.core.mongo.dto.DataRelationDto;
import com.touchMind.core.mongo.dto.DataRelationWsDto;
import com.touchMind.core.mongo.dto.DataSourceWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.DataRelation;
import com.touchMind.core.mongo.model.DataRelationParams;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.repository.DataRelationParamsRepository;
import com.touchMind.core.mongo.repository.DataRelationRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.DataRelationService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.form.DataRelationForm;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/dataRelation")
public class DataRelationController extends BaseController {

    public static final String ADMIN_DATA_RELATION = "/admin/dataRelation";
    Logger logger = LoggerFactory.getLogger(DataRelationController.class);
    @Autowired
    private DataRelationRepository dataRelationRepository;
    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private DataRelationService dataRelationService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private DataRelationParamsRepository dataRelationParamsRepository;

    @PostMapping
    @ResponseBody
    public DataRelationWsDto getAllModels(@RequestBody DataRelationWsDto dataRelationWsDto) {
        Pageable pageable = getPageable(dataRelationWsDto.getPage(), dataRelationWsDto.getSizePerPage(), dataRelationWsDto.getSortDirection(), dataRelationWsDto.getSortField());
        DataRelationDto dataRelationDto = CollectionUtils.isNotEmpty(dataRelationWsDto.getDataRelations()) ? dataRelationWsDto.getDataRelations().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(dataRelationDto, dataRelationWsDto.getOperator());
        DataRelation dataRelation = dataRelationDto != null ? modelMapper.map(dataRelationDto, DataRelation.class) : null;
        Page<DataRelation> page = isSearchActive(dataRelation) != null ? dataRelationRepository.findAll(Example.of(dataRelation, exampleMatcher), pageable) : dataRelationRepository.findAll(pageable);
        Type listType = new TypeToken<List<DataRelationDto>>() {
        }.getType();
        dataRelationWsDto.setDataRelations(modelMapper.map(page.getContent(), listType));
        dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
        dataRelationWsDto.setTotalPages(page.getTotalPages());
        dataRelationWsDto.setTotalRecords(page.getTotalElements());
        dataRelationWsDto.setAttributeList(getConfiguredAttributes(dataRelationWsDto.getNode()));
        dataRelationWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.DATA_RELATION));
        return dataRelationWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public DataRelationWsDto getActiveDataRelation() {
        DataRelationWsDto dataRelationWsDto = new DataRelationWsDto();
        dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
        Type listType = new TypeToken<List<DataRelationDto>>() {
        }.getType();
        dataRelationWsDto.setDataRelations(modelMapper.map(dataRelationRepository.findByStatusOrderByIdentifier(true), listType));
        return dataRelationWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody DataRelationWsDto dataRelationWsDto) {
        return getConfiguredAttributes(dataRelationWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new DataRelation());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.DATA_RELATION);
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody DataRelationDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(dataRelationRepository.findByIdentifier(recordId), DataRelationDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public DataRelationWsDto handleEdit(@RequestBody DataRelationWsDto request, @ModelAttribute("editForm") DataRelationForm dataRelationForm, Model model, BindingResult result) {
        return dataRelationService.handelEdit(request);
    }

    private List<DataRelationParams> getDataRelationParamMappings(DataRelationForm dataRelationForm) {
        List<DataRelationParams> sourceTargetParamMappings = new ArrayList<>();
        for (DataRelationParams dataRelationParam : dataRelationForm.getDataRelationParams()) {
            DataRelationParams dataRelationParams = modelMapper.map(dataRelationParam, DataRelationParams.class);
            if (StringUtils.isNotEmpty(dataRelationParams.getSourceKeyOne())) {
                sourceTargetParamMappings.add(dataRelationParams);
            }
        }
        return sourceTargetParamMappings;
    }

    @GetMapping("/add")
    @ResponseBody
    public DataRelationWsDto addDataSource() {
        DataRelationWsDto dataRelationWsDto = new DataRelationWsDto();
        dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
        dataRelationWsDto.setExistingParamsCount(0);
        return dataRelationWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public DataRelationWsDto deleteDataSource(@RequestBody DataRelationWsDto dataRelationWsDto) {
        for (DataRelationDto dataRelationDto : dataRelationWsDto.getDataRelations()) {
            dataRelationRepository.deleteByIdentifier(dataRelationDto.getIdentifier());
        }
        dataRelationWsDto.setMessage("Data deleted successfully");
        dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
        return dataRelationWsDto;
    }

    @PostMapping("/getDatasourceParamsForId")
    @ResponseBody
    public DataSourceWsDto getDatasourceParamsForId(@RequestBody DataRelationDto dataRelationDto) {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        DataSource dataSource = dataSourceRepository.findByIdentifier(dataRelationDto.getIdentifier());
        Set<String> params = new HashSet<>();
        params.addAll(dataSource.getSrcInputParams());
        params.addAll(dataSource.getDataSourceInputs().stream().map(dataSourceInput -> dataSourceInput.getFieldName()).collect(Collectors.toList()));
        dataSourceWsDto.setParams(params);
        return dataSourceWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public DataRelationWsDto getEdits(@RequestBody DataRelationWsDto request) {
        DataRelationWsDto dataRelationWsDto = new DataRelationWsDto();
        List<DataRelation> dataRelations = new ArrayList<>();
        for (DataRelationDto dataRelationDto : request.getDataRelations()) {
            dataRelations.add(dataRelationRepository.findByIdentifier(dataRelationDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<DataRelationDto>>() {
        }.getType();
        dataRelationWsDto.setDataRelations(modelMapper.map(dataRelations, listType));
        dataRelationWsDto.setBaseUrl(ADMIN_DATA_RELATION);
        return dataRelationWsDto;
    }


    @PostMapping("/upload")
    public DataSourceWsDto uploadFile(@RequestBody MultipartFile file) {
        DataSourceWsDto dataSourceWsDto = new DataSourceWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.DATA_RELATION, EntityConstants.DATA_RELATION, dataSourceWsDto);
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
    public DataRelationWsDto uploadFile(@RequestBody DataRelationWsDto dataRelationWsDto) {

        try {
            dataRelationWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.DATA_RELATION, dataRelationWsDto.getHeaderFields()));
            return dataRelationWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
