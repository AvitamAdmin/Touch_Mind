package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.LocatorsGroupPriorityDto;
import com.touchMind.core.mongo.dto.LocatorsGroupPriorityWsDto;
import com.touchMind.core.mongo.model.LocatorsGroupPriority;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorsGroupPriorityRepository;
import com.touchMind.core.service.LocatorGroupPriorityService;
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
@RequestMapping("/admin/actionGroup")
public class ActionGroupController extends BaseController {
    public static final String ACTION_GROUP_PRIORITY = "/admin/actionGroup";
    private final Logger logger = LoggerFactory.getLogger(ActionGroupController.class);
    @Autowired
    LocatorGroupPriorityService locatorGroupPriorityService;
    @Autowired
    private FileExportService fileExportService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private LocatorsGroupPriorityRepository locatorsGroupPriorityRepository;

    @PostMapping
    @ResponseBody
    public LocatorsGroupPriorityWsDto getAllModels(@RequestBody LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto) {
        Pageable pageable = getPageable(locatorsGroupPriorityWsDto.getPage(), locatorsGroupPriorityWsDto.getSizePerPage(), locatorsGroupPriorityWsDto.getSortDirection(), locatorsGroupPriorityWsDto.getSortField());
        LocatorsGroupPriorityDto locatorsGroupPriorityDto = CollectionUtils.isNotEmpty(locatorsGroupPriorityWsDto.getLocatorsGroupPriorityDtoList()) ? locatorsGroupPriorityWsDto.getLocatorsGroupPriorityDtoList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(locatorsGroupPriorityDto, locatorsGroupPriorityWsDto.getOperator());
        LocatorsGroupPriority locatorsGroupPriority = locatorsGroupPriorityDto != null ? modelMapper.map(locatorsGroupPriorityDto, LocatorsGroupPriority.class) : null;
        Page<LocatorsGroupPriority> page = isSearchActive(locatorsGroupPriority) != null ? locatorsGroupPriorityRepository.findAll(Example.of(locatorsGroupPriority, exampleMatcher), pageable) : locatorsGroupPriorityRepository.findAll(pageable);
        Type listType = new TypeToken<List<LocatorsGroupPriorityDto>>() {
        }.getType();
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(page.getContent(), listType));
        locatorsGroupPriorityWsDto.setBaseUrl(ACTION_GROUP_PRIORITY);
        locatorsGroupPriorityWsDto.setTotalPages(page.getTotalPages());
        locatorsGroupPriorityWsDto.setTotalRecords(page.getTotalElements());
        locatorsGroupPriorityWsDto.setAttributeList(getConfiguredAttributes(locatorsGroupPriorityWsDto.getNode()));
        return locatorsGroupPriorityWsDto;
    }

    @GetMapping("/get")
    @ResponseBody
    public LocatorsGroupPriorityWsDto getActiveLocatorGroupPriorities() {
        LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto = new LocatorsGroupPriorityWsDto();
        locatorsGroupPriorityWsDto.setBaseUrl(ACTION_GROUP_PRIORITY);
        Type listType = new TypeToken<List<LocatorsGroupPriorityDto>>() {
        }.getType();
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(locatorsGroupPriorityRepository.findByStatusOrderByIdentifier(true), listType));
        return locatorsGroupPriorityWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public LocatorsGroupPriorityWsDto handleEdit(@RequestBody LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto) {
        return locatorGroupPriorityService.handleEdit(locatorsGroupPriorityWsDto);
    }

    @GetMapping("/add")
    @ResponseBody
    public LocatorsGroupPriorityWsDto addLocatorsGroupPriority() {
        LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto = new LocatorsGroupPriorityWsDto();
        locatorsGroupPriorityWsDto.setBaseUrl(ACTION_GROUP_PRIORITY);
        Type listType = new TypeToken<List<LocatorsGroupPriorityDto>>() {
        }.getType();
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(locatorsGroupPriorityRepository.findByStatus(true), listType));
        return locatorsGroupPriorityWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody LocatorsGroupPriorityDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(locatorsGroupPriorityRepository.findByIdentifier(recordId), LocatorsGroupPriorityDto.class);
    }

    @PostMapping("/delete")
    @ResponseBody
    public LocatorsGroupPriorityWsDto deleteLocatorsGroupPriority(@RequestBody LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto) {
        for (LocatorsGroupPriorityDto locatorsGroupPriorityDto : locatorsGroupPriorityWsDto.getLocatorsGroupPriorityDtoList()) {
            locatorsGroupPriorityRepository.deleteByIdentifier(locatorsGroupPriorityDto.getIdentifier());
        }
        locatorsGroupPriorityWsDto.setMessage("Data deleted successfully!!");
        locatorsGroupPriorityWsDto.setBaseUrl(ACTION_GROUP_PRIORITY);
        return locatorsGroupPriorityWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public LocatorsGroupPriorityWsDto editMultiple(@RequestBody LocatorsGroupPriorityWsDto request) {
        LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto = new LocatorsGroupPriorityWsDto();
        List<LocatorsGroupPriority> locatorsGroupPriorities = new ArrayList<>();
        for (LocatorsGroupPriorityDto locatorsGroupPriorityDto : request.getLocatorsGroupPriorityDtoList()) {
            locatorsGroupPriorities.add(locatorsGroupPriorityRepository.findByIdentifier(locatorsGroupPriorityDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<LocatorsGroupPriorityDto>>() {
        }.getType();
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(locatorsGroupPriorities, listType));
        locatorsGroupPriorityWsDto.setRedirectUrl(ACTION_GROUP_PRIORITY);
        locatorsGroupPriorityWsDto.setBaseUrl(ACTION_GROUP_PRIORITY);
        return locatorsGroupPriorityWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public LocatorsGroupPriorityWsDto uploadFile(@RequestBody LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto) {

        try {
            locatorsGroupPriorityWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.LOCATOR_GROUP_PRIORITY, locatorsGroupPriorityWsDto.getHeaderFields()));
            return locatorsGroupPriorityWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/upload")
    public LocatorsGroupPriorityWsDto uploadFile(@RequestBody MultipartFile file) {
        LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto = new LocatorsGroupPriorityWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.LOCATOR_GROUP_PRIORITY, EntityConstants.LOCATOR_GROUP_PRIORITY, locatorsGroupPriorityWsDto);
            if (StringUtils.isEmpty(locatorsGroupPriorityWsDto.getMessage())) {
                locatorsGroupPriorityWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return locatorsGroupPriorityWsDto;
    }
}
