package com.touchmind.web.controllers.admin.environments;

import com.touchmind.core.mongo.dto.EnvironmentDto;
import com.touchmind.core.mongo.dto.EnvironmentWsDto;
import com.touchmind.core.mongo.model.Environment;
import com.touchmind.core.mongo.repository.EnvironmentRepository;
import com.touchmind.core.service.EnvironmentService;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
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

//    @Autowired
//    private FileImportService fileImportService;
//
//    @Autowired
//    private FileExportService fileExportService;

    @PostMapping
    @ResponseBody
    public EnvironmentWsDto getAll(@RequestBody EnvironmentWsDto environmentWsDto) {
        Pageable pageable = getPageable(environmentWsDto.getPage(), environmentWsDto.getSizePerPage(), environmentWsDto.getSortDirection(), environmentWsDto.getSortField());
        EnvironmentDto environmentDto = CollectionUtils.isNotEmpty(environmentWsDto.getEnvironments()) ? environmentWsDto.getEnvironments().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(environmentDto, environmentWsDto.getOperator());
        Environment environment = environmentDto != null ? modelMapper.map(environmentDto, Environment.class) : null;
        Page<Environment> page = isSearchActive(environment) != null ? environmentRepository.findAll(Example.of(environment, exampleMatcher), pageable) : environmentRepository.findAll(pageable);
        environmentWsDto.setEnvironments(modelMapper.map(page.getContent(), List.class));
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setTotalPages(page.getTotalPages());
        environmentWsDto.setTotalRecords(page.getTotalElements());
     //   environmentWsDto.setAttributeList(getConfiguredAttributes(environmentWsDto.getNode()));
        return environmentWsDto;
    }

//    @GetMapping("/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new Environment());
//    }

    @GetMapping("/get")
    @ResponseBody
    public EnvironmentWsDto getActiveEnvironments() {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setEnvironments(modelMapper.map(environmentRepository.findByStatusOrderByIdentifier(true), List.class));
        return environmentWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public EnvironmentWsDto handleEdit(@RequestBody EnvironmentWsDto request) {
        return environmentService.handleEdit(request);
    }

//    @PostMapping("/add")
//    @ResponseBody
//    public EnvironmentWsDto add() {
//        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
//        environmentWsDto.setExistingEnvironmentCount(0);
//        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
//        return environmentWsDto;
//    }

    @PostMapping("/getedits")
    @ResponseBody
    public EnvironmentWsDto getEdits(@RequestBody EnvironmentWsDto request) {
        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
        List<Environment> environments = new ArrayList<>();
        for (EnvironmentDto environmentDto : request.getEnvironments()) {
            environments.add(environmentRepository.findByRecordId(environmentDto.getRecordId()));
        }
        environmentWsDto.setEnvironments(modelMapper.map(environments, List.class));
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setRedirectUrl("");
        return environmentWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public EnvironmentWsDto delete(@RequestBody EnvironmentWsDto environmentWsDto) {
        for (EnvironmentDto environmentDto : environmentWsDto.getEnvironments()) {
            environmentRepository.deleteByRecordId(environmentDto.getRecordId());
        }
        environmentWsDto.setBaseUrl(ADMIN_ENVIRONMENT);
        environmentWsDto.setMessage("Delete Success");
        return environmentWsDto;
    }

//    @GetMapping("/export")
//    @ResponseBody
//    public EnvironmentWsDto uploadFile() {
//        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
//        try {
//            environmentWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.ENVIRONMENT));
//            return environmentWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
//
//    @PostMapping("/upload")
//    public EnvironmentWsDto uploadFile(@RequestBody MultipartFile file) {
//        EnvironmentWsDto environmentWsDto = new EnvironmentWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.ENVIRONMENT, EntityConstants.ENVIRONMENT, environmentWsDto);
//            if (StringUtils.isEmpty(environmentWsDto.getMessage())) {
//                environmentWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return environmentWsDto;
//    }
}
