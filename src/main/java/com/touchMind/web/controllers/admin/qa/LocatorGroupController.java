package com.touchMind.web.controllers.admin.qa;

import com.touchMind.core.mongo.dto.LocatorGroupDto;
import com.touchMind.core.mongo.dto.LocatorGroupWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.LocatorGroup;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorGroupRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.LocatorGroupsService;
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
import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/qalocatorgroup")
public class LocatorGroupController extends BaseController {

    public static final String ADMIN_LOCATOR_GROUP = "/admin/qalocatorgroup";
    Logger logger = LoggerFactory.getLogger(LocatorGroupController.class);

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;
    @Autowired
    private LocatorGroupRepository locatorGroupRepository;
    @Autowired
    private LocatorGroupsService locatorGroupsService;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;

    @PostMapping
    @ResponseBody
    public LocatorGroupWsDto getLocatorGroup(@RequestBody LocatorGroupWsDto locatorGroupWsDto) {
        Pageable pageable = getPageable(locatorGroupWsDto.getPage(), locatorGroupWsDto.getSizePerPage(), locatorGroupWsDto.getSortDirection(), locatorGroupWsDto.getSortField());
        LocatorGroupDto locatorGroupDto = CollectionUtils.isNotEmpty(locatorGroupWsDto.getGroupsDtoList()) ? locatorGroupWsDto.getGroupsDtoList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(locatorGroupDto, locatorGroupWsDto.getOperator());
        LocatorGroup locatorGroup = locatorGroupDto != null ? modelMapper.map(locatorGroupDto, LocatorGroup.class) : null;
        Page<LocatorGroup> page = isSearchActive(locatorGroup) != null ? locatorGroupRepository.findAll(Example.of(locatorGroup, exampleMatcher), pageable) : locatorGroupRepository.findAll(pageable);
        Type listType = new TypeToken<List<LocatorGroupDto>>() {
        }.getType();
        locatorGroupWsDto.setGroupsDtoList(modelMapper.map(page.getContent(), listType));
        locatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        locatorGroupWsDto.setTotalPages(page.getTotalPages());
        locatorGroupWsDto.setTotalRecords(page.getTotalElements());
        locatorGroupWsDto.setAttributeList(getConfiguredAttributes(locatorGroupWsDto.getNode()));
        locatorGroupWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_PLAN));
        return locatorGroupWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new LocatorGroup());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.LOCATOR_GROUP);
    }

    @GetMapping("/copy")
    @ResponseBody
    public LocatorGroupWsDto copy(@RequestParam("recordId") String recordId) {
        LocatorGroupWsDto locatorGroupWsDto = new LocatorGroupWsDto();
        LocatorGroup locatorGroup = locatorGroupRepository.findByIdentifier(recordId);
        LocatorGroupDto locatorGroupDto = modelMapper.map(locatorGroup, LocatorGroupDto.class);
        locatorGroupDto.setIdentifier(null);
        locatorGroupDto.setCreationTime(new Date());
        locatorGroupDto.setLastModified(new Date());
        locatorGroupDto.setIdentifier("Copy_" + locatorGroup.getIdentifier());
        LocatorGroup clonedLocatorGroup = modelMapper.map(locatorGroupDto, LocatorGroup.class);
        locatorGroupRepository.save(clonedLocatorGroup);
        String id = String.valueOf(clonedLocatorGroup.getId().getTimestamp());
        if (locatorGroupRepository.findByIdentifier(id) != null) {
            id = id + new Random().nextInt(24565);
        }
        clonedLocatorGroup.setIdentifier(id);
        clonedLocatorGroup.setIdentifier("Copy_" + id + locatorGroup.getIdentifier());
        locatorGroupRepository.save(clonedLocatorGroup);
        locatorGroupWsDto.setMessage("Action Group cloned successfully!!");
        return locatorGroupWsDto;
    }

    @GetMapping("/getByGroupId")
    @ResponseBody
    public LocatorGroupDto getLocatorGroupForm(@RequestParam String groupId) {
        return modelMapper.map(locatorGroupRepository.findByIdentifier(groupId), LocatorGroupDto.class);
    }

    @GetMapping("/get")
    public LocatorGroupWsDto getActiveLocatorGroup() {
        LocatorGroupWsDto locatorGroupWsDto = new LocatorGroupWsDto();
        Type listType = new TypeToken<List<LocatorGroupDto>>() {
        }.getType();
        locatorGroupWsDto.setGroupsDtoList(modelMapper.map(locatorGroupRepository.findByStatusOrderByIdentifier(true), listType));
        locatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return locatorGroupWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody LocatorGroupDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(locatorGroupRepository.findByIdentifier(recordId), LocatorGroupDto.class);
    }

    @GetMapping("/add")
    @ResponseBody
    public LocatorGroupWsDto getLocatorForm() {
        LocatorGroupWsDto locatorGroupWsDto = new LocatorGroupWsDto();
        locatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return locatorGroupWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public LocatorGroupWsDto updateLocatorGroup(@RequestBody LocatorGroupWsDto request) {
        return locatorGroupsService.handleEdit(request);
    }

    @PostMapping("/delete")
    @ResponseBody
    public LocatorGroupWsDto deleteLocatorGroup(@RequestBody LocatorGroupWsDto locatorGroupWsDto) {
        for (LocatorGroupDto locatorGroupDto : locatorGroupWsDto.getGroupsDtoList()) {
            locatorGroupRepository.deleteByIdentifier(locatorGroupDto.getIdentifier());
        }
        locatorGroupWsDto.setMessage("Data deleted successfully!!");
        locatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return locatorGroupWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public LocatorGroupWsDto editMultiple(@RequestBody LocatorGroupWsDto request) {
        LocatorGroupWsDto locatorGroupWsDto = new LocatorGroupWsDto();
        List<LocatorGroup> locatorGroups = new ArrayList<>();
        for (LocatorGroupDto locatorGroupDto : request.getGroupsDtoList()) {
            locatorGroups.add(locatorGroupRepository.findByIdentifier(locatorGroupDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<LocatorGroupDto>>() {
        }.getType();
        locatorGroupWsDto.setGroupsDtoList(modelMapper.map(locatorGroups, listType));
        locatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        return locatorGroupWsDto;
    }

    @PostMapping("/upload")
    @ResponseBody
    public LocatorGroupWsDto uploadFile(@RequestBody MultipartFile file) {
        LocatorGroupWsDto locatorGroupWsDto = new LocatorGroupWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.LOCATOR_GROUP, EntityConstants.LOCATOR_GROUP, locatorGroupWsDto);
            if (StringUtils.isEmpty(locatorGroupWsDto.getMessage())) {
                locatorGroupWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return locatorGroupWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public LocatorGroupWsDto uploadFile(@RequestBody LocatorGroupWsDto locatorGroupWsDto) {

        try {
            locatorGroupWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.LOCATOR_GROUP, locatorGroupWsDto.getHeaderFields()));
            return locatorGroupWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


}
