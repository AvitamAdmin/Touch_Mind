package com.touchmind.web.controllers.admin.role;

import com.touchmind.core.mongo.dto.RoleDto;
import com.touchmind.core.mongo.dto.RoleWsDto;
import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.RoleRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.RoleService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

    public static final String ADMIN_ROLE = "/admin/role";
    Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;


    @PostMapping
    @ResponseBody
    public RoleWsDto getRoles(@RequestBody RoleWsDto roleWsDto) {
        Pageable pageable = getPageable(roleWsDto.getPage(), roleWsDto.getSizePerPage(), roleWsDto.getSortDirection(), roleWsDto.getSortField());
        RoleDto roleDto = CollectionUtils.isNotEmpty(roleWsDto.getRoles()) ? roleWsDto.getRoles().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(roleDto, roleWsDto.getOperator());
        Role role = roleDto != null ? modelMapper.map(roleDto, Role.class) : null;
        Page<Role> page = isSearchActive(role) != null ? roleRepository.findAll(Example.of(role, exampleMatcher), pageable) : roleRepository.findAll(pageable);
        roleWsDto.setRoles(modelMapper.map(page.getContent(), List.class));
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        roleWsDto.setTotalPages(page.getTotalPages());
        roleWsDto.setTotalRecords(page.getTotalElements());
        roleWsDto.setAttributeList(getConfiguredAttributes(roleWsDto.getNode()));
        roleWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.ROLE));
        return roleWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Role());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.ROLE);
    }

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody RoleDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(roleRepository.findByRecordId(recordId), RoleDto.class);
    }

    @GetMapping("/get")
    public RoleWsDto getActiveRoles() {
        RoleWsDto roleWsDto = new RoleWsDto();
        roleWsDto.setRoles(modelMapper.map(roleRepository.findByStatusOrderByIdentifier(true), List.class));
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        return roleWsDto;
    }

    @PostMapping("/edit")
    @ResponseBody
    public RoleWsDto handleEdit(@RequestBody RoleWsDto request) {
        return roleService.handleEdit(request);
    }

    @PostMapping("/copy")
    @ResponseBody
    public RoleWsDto handleCopy(@RequestBody RoleWsDto request) {
        return roleService.handelCopy(request);
    }

    @GetMapping("/add")
    @ResponseBody
    public RoleWsDto addUser(@ModelAttribute RoleDto roleForm) {
        RoleWsDto roleWsDto = new RoleWsDto();
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        return roleWsDto;
    }

    @PostMapping("/delete")
    @ResponseBody
    public RoleWsDto deleteRole(@RequestBody RoleWsDto roleWsDto) {
        for (RoleDto roleDto : roleWsDto.getRoles()) {
            roleRepository.deleteByRecordId(roleDto.getRecordId());
        }
        roleWsDto.setMessage("Data deleted Successfully");
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        return roleWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public RoleWsDto editMultiple(@RequestBody RoleWsDto request) {
        RoleWsDto roleWsDto = new RoleWsDto();
        List<Role> roles = new ArrayList<>();
        for (RoleDto roleDto : request.getRoles()) {
            roles.add(roleRepository.findByRecordId(roleDto.getRecordId()));
        }
        roleWsDto.setRoles(modelMapper.map(roles, List.class));
        roleWsDto.setRedirectUrl("/admin/role");
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        return roleWsDto;
    }

    @PostMapping("/upload")
    public RoleWsDto uploadFile(@RequestBody MultipartFile file) {
        RoleWsDto roleWsDto = new RoleWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.ROLE, EntityConstants.ROLE, roleWsDto);
            if (StringUtils.isEmpty(roleWsDto.getMessage())) {
                roleWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return roleWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public RoleWsDto uploadFile() {
        RoleWsDto roleWsDto = new RoleWsDto();
        try {
            roleWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.ROLE));
            return roleWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
