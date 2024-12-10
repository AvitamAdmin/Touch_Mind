package com.touchmind.web.controllers.system;

import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.SystemRoleDto;
import com.touchmind.core.mongo.dto.SystemRoleWsDto;
import com.touchmind.core.mongo.model.SystemRole;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.mongo.repository.SystemRoleRepository;
import com.touchmind.core.service.SystemRoleService;
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
@RequestMapping("/admin/systemrole")
public class SystemRoleController extends BaseController {

    public static final String ADMIN_SYSTEMROLE = "/admin/systemrole";

    Logger logger = LoggerFactory.getLogger(SystemRoleController.class);
    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private SystemRoleService systemRoleService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseBody
    public SystemRoleWsDto getAllSystems(@RequestBody SystemRoleWsDto systemRoleWsDto) throws IOException {
        Pageable pageable = getPageable(systemRoleWsDto.getPage(), systemRoleWsDto.getSizePerPage(), systemRoleWsDto.getSortDirection(), systemRoleWsDto.getSortField());
        SystemRoleDto systemRoleDto = CollectionUtils.isNotEmpty(systemRoleWsDto.getSystemRoles()) ? systemRoleWsDto.getSystemRoles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(systemRoleDto, systemRoleWsDto.getOperator());
        SystemRole systemRole = systemRoleDto != null ? modelMapper.map(systemRoleDto, SystemRole.class) : null;
        Page<SystemRole> page = isSearchActive(systemRole) != null ? systemRoleRepository.findAll(Example.of(systemRole, exampleMatcher), pageable) : systemRoleRepository.findAll(pageable);
        systemRoleWsDto.setSystemRoles(modelMapper.map(page.getContent(), List.class));
        systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
        systemRoleWsDto.setTotalPages(page.getTotalPages());
        systemRoleWsDto.setTotalRecords(page.getTotalElements());
        systemRoleWsDto.setAttributeList(getConfiguredAttributes(systemRoleWsDto.getNode()));
        return systemRoleWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new SystemRole());
    }

    @GetMapping("/get")
    @ResponseBody
    public SystemRoleWsDto getActiveSystemRoles() {
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
        systemRoleWsDto.setSystemRoles(modelMapper.map(systemRoleRepository.findByStatusOrderByIdentifier(true), List.class));
        return systemRoleWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public SystemRoleWsDto handleEdit(@RequestBody SystemRoleWsDto request) throws IOException, InterruptedException {
        return systemRoleService.handleEdit(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public SystemRoleWsDto addSystem() {
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
        return systemRoleWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public SystemRoleWsDto deleteLibrary(@RequestBody SystemRoleWsDto systemRoleWsDto) throws IOException, InterruptedException {
        try {
            for (SystemRoleDto systemRoleDto : systemRoleWsDto.getSystemRoles()) {
                systemRoleRepository.deleteByRecordId(systemRoleDto.getRecordId());
            }
        } catch (Exception e) {
            return systemRoleWsDto;
        }
        systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
        systemRoleWsDto.setMessage("Data deleted successfully!!");
        return systemRoleWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public SystemRoleWsDto editMultiple(@RequestBody SystemRoleWsDto request) {
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        List<SystemRole> systemRoles = new ArrayList<>();
        for (SystemRoleDto systemRoleDto : request.getSystemRoles()) {
            systemRoles.add(systemRoleRepository.findByRecordId(systemRoleDto.getRecordId()));
        }
        systemRoleWsDto.setSystemRoles(modelMapper.map(systemRoles, List.class));
        systemRoleWsDto.setRedirectUrl("/admin/systemrole");
        systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
        return systemRoleWsDto;
    }

    @PostMapping("/upload")
    public SystemRoleWsDto uploadFile(@RequestBody MultipartFile file) {
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.SYSTEM_ROLE, EntityConstants.SYSTEM_ROLE, systemRoleWsDto);
            if (StringUtils.isEmpty(systemRoleWsDto.getMessage())) {
                systemRoleWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return systemRoleWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public SystemRoleWsDto uploadFile() {
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        try {
            systemRoleWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.SYSTEM_ROLE));
            return systemRoleWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
