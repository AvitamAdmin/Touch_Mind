package com.cheil.web.controllers.admin.role;

import com.cheil.core.mongo.dto.RoleDto;
import com.cheil.core.mongo.dto.RoleWsDto;
import com.cheil.core.mongo.dto.SearchDto;
import com.cheil.core.mongo.model.Role;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.RoleRepository;
import com.cheil.core.service.RoleService;
import com.cheil.fileimport.service.FileExportService;
import com.cheil.fileimport.service.FileImportService;
import com.cheil.fileimport.strategies.EntityType;
import com.cheil.form.RoleForm;
import com.cheil.web.controllers.BaseController;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
        return roleWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new Role());
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
    public RoleWsDto addUser(@ModelAttribute RoleForm roleForm) {
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
