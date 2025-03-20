package com.touchMind.web.controllers.admin.role;

import com.touchMind.core.mongo.dto.RoleDto;
import com.touchMind.core.mongo.dto.RoleWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.model.Role;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.NodeRepository;
import com.touchMind.core.mongo.repository.RoleRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.RoleService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.form.RoleForm;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        roleWsDto.setRoles(modelMapper.map(page.getContent(), listType));
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        roleWsDto.setTotalPages(page.getTotalPages());
        roleWsDto.setTotalRecords(page.getTotalElements());
        roleWsDto.setAttributeList(getConfiguredAttributes(roleWsDto.getNode()));
        roleWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.ROLE));
        return roleWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody RoleWsDto roleWsDto) {
        return getConfiguredAttributes(roleWsDto.getNode());
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

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody RoleDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(roleRepository.findByIdentifier(recordId), RoleDto.class);
    }

    @GetMapping("/get")
    public RoleWsDto getActiveRoles() {
        RoleWsDto roleWsDto = new RoleWsDto();
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        roleWsDto.setRoles(modelMapper.map(roleRepository.findByStatusOrderByIdentifier(true), listType));
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
            roleRepository.deleteByIdentifier(roleDto.getIdentifier());
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
            roles.add(roleRepository.findByIdentifier(roleDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        roleWsDto.setRoles(modelMapper.map(roles, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public RoleWsDto uploadFile(@RequestBody RoleWsDto roleWsDto) {
        try {
            roleWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.ROLE, roleWsDto.getHeaderFields()));
            return roleWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
