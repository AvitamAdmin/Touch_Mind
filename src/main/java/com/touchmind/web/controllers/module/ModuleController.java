package com.touchmind.web.controllers.module;

import com.touchmind.core.mongo.dto.ModuleDto;
import com.touchmind.core.mongo.dto.ModuleWsDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SystemWsDto;
import com.touchmind.core.mongo.model.Module;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.ModuleRepository;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.service.ModuleService;
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
@RequestMapping("/admin/module")
public class ModuleController extends BaseController {

    public static final String ADMIN_MODULE = "/admin/module";

    Logger logger = LoggerFactory.getLogger(ModuleController.class);
    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public ModuleWsDto getAllModules(@RequestBody ModuleWsDto moduleWsDto) throws IOException {
        Pageable pageable = getPageable(moduleWsDto.getPage(), moduleWsDto.getSizePerPage(), moduleWsDto.getSortDirection(), moduleWsDto.getSortField());
        ModuleDto moduleDto = CollectionUtils.isNotEmpty(moduleWsDto.getModules()) ? moduleWsDto.getModules().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(moduleDto, moduleWsDto.getOperator());
        Module module = moduleDto != null ? modelMapper.map(moduleDto, Module.class) : null;
        Page<Module> page = isSearchActive(module) != null ? moduleRepository.findAll(Example.of(module, exampleMatcher), pageable) : moduleRepository.findAll(pageable);
        moduleWsDto.setModules(modelMapper.map(page.getContent(), List.class));
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        moduleWsDto.setTotalPages(page.getTotalPages());
        moduleWsDto.setTotalRecords(page.getTotalElements());
        moduleWsDto.setAttributeList(getConfiguredAttributes(moduleWsDto.getNode()));
        return moduleWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Module());
    }

    @GetMapping("/get")
    @ResponseBody
    public ModuleWsDto getActiveModules() {
        ModuleWsDto moduleWsDto = new ModuleWsDto();
        moduleWsDto.setModules(modelMapper.map(moduleRepository.findByStatusOrderByIdentifier(true), List.class));
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        return moduleWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public ModuleWsDto handleEdit(@RequestBody ModuleWsDto request) throws IOException, InterruptedException {
        return moduleService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SystemWsDto addLibrary() {
        SystemWsDto systemWsDto = new SystemWsDto();
        systemWsDto.setSystems(modelMapper.map(systemRepository.findByStatusOrderByIdentifier(true), List.class));
        systemWsDto.setBaseUrl(ADMIN_MODULE);
        return systemWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public ModuleWsDto deleteLibrary(@RequestBody ModuleWsDto moduleWsDto) throws IOException, InterruptedException {
        try {
            for (ModuleDto moduleDto : moduleWsDto.getModules()) {
                moduleRepository.deleteByRecordId(moduleDto.getRecordId());
            }
        } catch (Exception e) {
            return moduleWsDto;
        }
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        moduleWsDto.setMessage("Data deleted successfully");
        return moduleWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public ModuleWsDto editMultiple(@RequestBody ModuleWsDto request) {
        ModuleWsDto moduleWsDto = new ModuleWsDto();
        List<Module> moduleList = new ArrayList<>();
        for (ModuleDto moduleDto : request.getModules()) {
            moduleList.add(moduleRepository.findByRecordId(moduleDto.getRecordId()));
        }
        moduleWsDto.setModules(modelMapper.map(moduleList, List.class));
        moduleWsDto.setBaseUrl(ADMIN_MODULE);
        moduleWsDto.setRedirectUrl("/admin/module");
        return moduleWsDto;
    }

    @PostMapping("/upload")
    public ModuleWsDto uploadFile(@RequestBody MultipartFile file) {
        ModuleWsDto moduleWsDto = new ModuleWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.MODULE, EntityConstants.MODULE, moduleWsDto);
            if (StringUtils.isEmpty(moduleWsDto.getMessage())) {
                moduleWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return moduleWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public ModuleWsDto uploadFile() {
        ModuleWsDto moduleWsDto = new ModuleWsDto();
        try {
            moduleWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.MODULE));
            return moduleWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}