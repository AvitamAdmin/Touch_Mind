package com.touchMind.web.controllers.admin.environments;

import com.touchMind.core.mongo.dto.EnvironmentDto;
import com.touchMind.core.mongo.dto.EnvironmentWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Environment;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.EnvironmentRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.EnvironmentService;
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
@RequestMapping("/admin/environment")
public class EnvironmentController extends BaseController {

    public static final String ADMIN_ENVIRONMENT = "/admin/environment";
    Logger logger = LoggerFactory.getLogger(EnvironmentController.class);

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private BaseService baseService;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;

    @PostMapping
    @ResponseBody
    public EnvironmentWsDto getAll(@RequestBody EnvironmentWsDto environmentWsDto) {
        Pageable pageable = getPageable(environmentWsDto.getPage(), environmentWsDto.getSizePerPage(), environmentWsDto.getSortDirection(), environmentWsDto.getSortField());
        EnvironmentDto environmentDto = CollectionUtils.isNotEmpty(environmentWsDto.getEnvironments()) ? environmentWsDto.getEnvironments().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(environmentDto, environmentWsDto.getOperator());
        Environment environment = environmentDto != null ? modelMapper.map(environmentDto, Environment.class) : null;
        Page<Environment> page = isSearchActive(environment) != null ? environmentRepository.findAll(Example.of(environment, exampleMatcher), pageable) : environmentRepository.findAll(pageable);
        Type listType = new TypeToken<List<EnvironmentDto>>() {
        }.getType();
        environmentWsDto.setEnvironments(modelMapper.map(page.getContent(), listType));
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setTotalPages(page.getTotalPages());
        environmentWsDto.setTotalRecords(page.getTotalElements());
        environmentWsDto.setAttributeList(getConfiguredAttributes(environmentWsDto.getNode()));
        environmentWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.ENVIRONMENT));
        return environmentWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody EnvironmentWsDto environmentWsDto) {
        return getConfiguredAttributes(environmentWsDto.getNode());
    }


    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Environment());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.ENVIRONMENT);
    }

    @GetMapping("/get")
    @ResponseBody
    public EnvironmentWsDto getActiveEnvironments() {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        Type listType = new TypeToken<List<EnvironmentDto>>() {
        }.getType();
        environmentWsDto.setEnvironments(modelMapper.map(environmentRepository.findByStatusOrderByIdentifier(true), listType));
        return environmentWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody EnvironmentDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(environmentRepository.findByIdentifier(recordId), EnvironmentDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public EnvironmentWsDto handleEdit(@RequestBody EnvironmentWsDto request) {
        return environmentService.handelEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public EnvironmentWsDto add() {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        environmentWsDto.setExistingEnvironmentCount(0);
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        return environmentWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public EnvironmentWsDto getEdits(@RequestBody EnvironmentWsDto request) {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        List<Environment> environments = new ArrayList<>();
        for (EnvironmentDto environmentDto : request.getEnvironments()) {
            environments.add(environmentRepository.findByIdentifier(environmentDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<EnvironmentDto>>() {
        }.getType();
        environmentWsDto.setEnvironments(modelMapper.map(environments, listType));
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setRedirectUrl("");
        return environmentWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public EnvironmentWsDto delete(@RequestBody EnvironmentWsDto environmentWsDto) {
        for (EnvironmentDto environmentDto : environmentWsDto.getEnvironments()) {
            environmentRepository.deleteByIdentifier(environmentDto.getIdentifier());
        }
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setMessage("Data deleted successfully!!");
        return environmentWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public EnvironmentWsDto uploadFile(@RequestBody EnvironmentWsDto environmentWsDto) {

        try {
            environmentWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.ENVIRONMENT, environmentWsDto.getHeaderFields()));
            return environmentWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    public EnvironmentWsDto uploadFile(@RequestBody MultipartFile file) {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.ENVIRONMENT, EntityConstants.ENVIRONMENT, environmentWsDto);
            if (StringUtils.isEmpty(environmentWsDto.getMessage())) {
                environmentWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return environmentWsDto;
    }
}
