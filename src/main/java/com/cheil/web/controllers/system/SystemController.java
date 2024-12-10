package com.cheil.web.controllers.system;

import com.cheil.core.mongo.dto.SearchDto;
import com.cheil.core.mongo.dto.SystemDto;
import com.cheil.core.mongo.dto.SystemWsDto;
import com.cheil.core.mongo.model.System;
import com.cheil.core.mongo.repository.CatalogRepository;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.ModuleRepository;
import com.cheil.core.mongo.repository.SiteRepository;
import com.cheil.core.mongo.repository.SubsidiaryRepository;
import com.cheil.core.mongo.repository.SystemRepository;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.SiteService;
import com.cheil.core.service.SubsidiaryService;
import com.cheil.core.service.SystemService;
import com.cheil.fileimport.service.FileExportService;
import com.cheil.fileimport.service.FileImportService;
import com.cheil.fileimport.strategies.EntityType;
import com.cheil.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/system")
public class SystemController extends BaseController {

    public static final String ADMIN_SYSTEM = "/admin/system";
    Logger logger = LoggerFactory.getLogger(SystemController.class);
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private SubsidiaryService subsidiaryService;
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

    @PostMapping
    @ResponseBody
    public SystemWsDto getAllSystems(@RequestBody SystemWsDto systemWsDto) throws IOException {
        Pageable pageable = getPageable(systemWsDto.getPage(), systemWsDto.getSizePerPage(), systemWsDto.getSortDirection(), systemWsDto.getSortField());
        SystemDto systemDto = CollectionUtils.isNotEmpty(systemWsDto.getSystems()) ? systemWsDto.getSystems().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(systemDto, systemWsDto.getOperator());
        System system = systemDto != null ? modelMapper.map(systemDto, System.class) : null;
        Page<System> page = isSearchActive(system) != null ? systemRepository.findAll(Example.of(system, exampleMatcher), pageable) : systemRepository.findAll(pageable);
        systemWsDto.setSystems(modelMapper.map(page.getContent(), List.class));
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        systemWsDto.setTotalPages(page.getTotalPages());
        systemWsDto.setTotalRecords(page.getTotalElements());
        systemWsDto.setAttributeList(getConfiguredAttributes(systemWsDto.getNode()));
        return systemWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new System());
    }

    @GetMapping("/get")
    @ResponseBody
    public SystemWsDto getActiveSystems() {
        SystemWsDto systemWsDto = new SystemWsDto();
        systemWsDto.setSystems(modelMapper.map(systemRepository.findByStatusOrderByIdentifier(true), List.class));
        systemWsDto.setBaseUrl(ADMIN_SYSTEM);
        return systemWsDto;
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
            systems.add(systemRepository.findByRecordId(systemDto.getRecordId()));
        }
        systemWsDto.setSystems(modelMapper.map(systems, List.class));
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
                systemRepository.deleteByRecordId(systemDto.getRecordId());
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

    @GetMapping("/export")
    @ResponseBody
    public SystemWsDto uploadFile() {
        SystemWsDto systemWsDto = new SystemWsDto();
        try {
            systemWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SYSTEM));
            return systemWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
