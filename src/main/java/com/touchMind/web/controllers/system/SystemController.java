package com.touchMind.web.controllers.system;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.SystemDto;
import com.touchMind.core.mongo.dto.SystemWsDto;
import com.touchMind.core.mongo.model.System;
import com.touchMind.core.mongo.repository.CatalogRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.ModuleRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SiteService;
import com.touchMind.core.service.SystemService;
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
import org.springframework.core.env.Environment;
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
@RequestMapping("/admin/system")
public class SystemController extends BaseController {

    public static final String ADMIN_SYSTEM = "/admin/system";
    Logger logger = LoggerFactory.getLogger(SystemController.class);
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
//    @Autowired
//    private SubsidiaryService subsidiaryService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;

    @Autowired
    private Environment env;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public SystemWsDto getAllSystems(@RequestBody SystemWsDto systemWsDto) throws IOException {
        Pageable pageable = getPageable(systemWsDto.getPage(), systemWsDto.getSizePerPage(), systemWsDto.getSortDirection(), systemWsDto.getSortField());
        SystemDto systemDto = CollectionUtils.isNotEmpty(systemWsDto.getSystems()) ? systemWsDto.getSystems().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(systemDto, systemWsDto.getOperator());
        System system = systemDto != null ? modelMapper.map(systemDto, System.class) : null;
        Page<System> page = isSearchActive(system) != null ? systemRepository.findAll(Example.of(system, exampleMatcher), pageable) : systemRepository.findAll(pageable);
        Type listType = new TypeToken<List<SystemDto>>() {
        }.getType();
        systemWsDto.setSystems(modelMapper.map(page.getContent(), listType));
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        systemWsDto.setTotalPages(page.getTotalPages());
        systemWsDto.setTotalRecords(page.getTotalElements());
        systemWsDto.setAttributeList(getConfiguredAttributes(systemWsDto.getNode()));
        systemWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.SYSTEM));
        return systemWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody SystemWsDto systemWsDto) {
        return getConfiguredAttributes(systemWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new System());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SYSTEM);
    }

    @GetMapping("/get")
    @ResponseBody
    public SystemWsDto getActiveSystems() {
        SystemWsDto systemWsDto = new SystemWsDto();
        Type listType = new TypeToken<List<SystemDto>>() {
        }.getType();
        systemWsDto.setSystems(modelMapper.map(systemRepository.findByStatusOrderByIdentifier(true), listType));
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        return systemWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody SystemDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(systemRepository.findByIdentifier(recordId), SystemDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public SystemWsDto handleEdit(@RequestBody SystemWsDto request) {
        return systemService.handleEdit(request);
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SystemWsDto editMultiple(@RequestBody SystemWsDto request) {
        SystemWsDto systemWsDto = new SystemWsDto();
        List<System> systems = new ArrayList<>();
        for (SystemDto systemDto : request.getSystems()) {
            systems.add(systemRepository.findByIdentifier(systemDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<SystemDto>>() {
        }.getType();
        systemWsDto.setSystems(modelMapper.map(systems, listType));
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        return systemWsDto;
    }

    @GetMapping("/add")
    @ResponseBody
    public SystemWsDto addSystem() {
        SystemWsDto systemWsDto = new SystemWsDto();
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        return systemWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public SystemWsDto deleteLibrary(@RequestBody SystemWsDto systemWsDto) throws IOException, InterruptedException {
        try {
            for (SystemDto systemDto : systemWsDto.getSystems()) {
                systemRepository.deleteByIdentifier(systemDto.getIdentifier());
            }
        } catch (Exception e) {
            return systemWsDto;
        }
        systemWsDto.setMessage("Data deleted successfully!!");
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        return systemWsDto;
    }

    @PostMapping("/upload")
    public SystemWsDto uploadFile(@RequestBody MultipartFile file) {
        SystemWsDto systemWsDto = new SystemWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SYSTEM, EntityConstants.SYSTEM, systemWsDto);
            if (StringUtils.isEmpty(systemWsDto.getMessage())) {
                systemWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return systemWsDto;
    }

    @PostMapping("/export")
    @ResponseBody
    public SystemWsDto uploadFile(@RequestBody SystemWsDto systemWsDto) {

        try {
            systemWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SYSTEM, systemWsDto.getHeaderFields()));
            return systemWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
