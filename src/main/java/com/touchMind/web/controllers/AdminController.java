package com.touchMind.web.controllers;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.UserDto;
import com.touchMind.core.mongo.dto.UserWsDto;
import com.touchMind.core.mongo.model.CommonFields;
import com.touchMind.core.mongo.model.Role;
import com.touchMind.core.mongo.model.User;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.UserRepository;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.UserService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    public static final String ADMIN_USER = "/admin/user";

    Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private FileImportService fileImportService;

    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private GenericImportRepository genericImportRepository;

    @PostMapping("/user")
    @ResponseBody
    public UserWsDto getAllUsers(@RequestBody UserWsDto userWsDto) {
        Pageable pageable = getPageable(userWsDto.getPage(), userWsDto.getSizePerPage(), userWsDto.getSortDirection(), userWsDto.getSortField());
        UserDto userDto = CollectionUtils.isNotEmpty(userWsDto.getUsers()) ? userWsDto.getUsers().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(userDto, userWsDto.getOperator());
        User user = userDto != null ? modelMapper.map(userDto, User.class) : null;
        Page<User> page = isSearchActive(user) != null ? userRepository.findAll(Example.of(user, exampleMatcher), pageable) : userRepository.findAll(pageable);
        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();
        userWsDto.setUsers(modelMapper.map(page.getContent(), listType));
        userWsDto.setTotalPages(page.getTotalPages());
        userWsDto.setTotalRecords(page.getTotalElements());
        userWsDto.setAttributeList(getConfiguredAttributes(userWsDto.getNode()));
        userWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.USER));
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody CommonFields getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(genericImportRepository.findByIdentifier(recordId), CommonFields.class);
    }


    @PostMapping("/user/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody UserWsDto userWsDto) {
        return getConfiguredAttributes(userWsDto.getNode());
    }

    @GetMapping("/user/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new User());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.USER);
    }

    @PostMapping("/user/search")
    @ResponseBody
    public List<UserDto> findUser(@RequestBody UserDto userDto) {
        ExampleMatcher exampleMatcher = getMatcher(userDto, StringUtils.EMPTY);
        User user = userDto != null ? modelMapper.map(userDto, User.class) : null;
        if (user != null) {
            List<User> users = userRepository.findAll(Example.of(user, exampleMatcher));
            Type listType = new TypeToken<List<UserDto>>() {
            }.getType();
            return modelMapper.map(users, listType);
        }
        return new ArrayList<UserDto>();
    }

    @PostMapping("/user/get")
    @ResponseBody
    public UserDto getUser(@RequestBody UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername());
        boolean isAdmin = false;
        Set<String> roles = new HashSet<>();
        Set<String> subs = new HashSet<>();
        UserDto userDto1 = modelMapper.map(user, UserDto.class);
        if (CollectionUtils.isNotEmpty(user.getRoles())) {
            for (Role role : user.getRoles()) {
                if (role.getIdentifier().equalsIgnoreCase("ROLE_ADMIN")) {
                    isAdmin = true;
                }
                roles.add(role.getIdentifier());
            }
        }
//        if (CollectionUtils.isNotEmpty(user.getSubsidiaries())) {
//            for (Subsidiary subsidiary : user.getSubsidiaries()) {
//                subs.add(subsidiary.getIdentifier());
//            }
//        }
        userDto1.setAdmin(isAdmin);
        userDto1.setSubsidiaries(subs);
        userDto1.setRoles(roles);
        return userDto1;
    }

    @PostMapping("/user/getedits")
    @ResponseBody
    public UserWsDto editUser(@RequestBody UserWsDto request) {
        UserWsDto userWsDto = new UserWsDto();
        List<UserDto> userDtos = new ArrayList<>();
        userService.populateCommonFields(userWsDto);
        for (UserDto userDto : request.getUsers()) {
            User user = userRepository.findByIdentifier(userDto.getIdentifier());
            Set<String> roles = new HashSet<>();
            Set<String> subs = new HashSet<>();
            UserDto userDto1 = modelMapper.map(user, UserDto.class);
            if (CollectionUtils.isNotEmpty(user.getRoles())) {
                for (Role role : user.getRoles()) {
                    roles.add(role.getIdentifier());
                }
            }
//            if (CollectionUtils.isNotEmpty(user.getSubsidiaries())) {
//                for (Subsidiary subsidiary : user.getSubsidiaries()) {
//                    subs.add(String.valueOf(subsidiary.getIdentifier()));
//                }
//            }
            userDto1.setRoles(roles);
            userDto1.setSubsidiaries(subs);
            userDtos.add(userDto1);
        }
        userWsDto.setUsers(userDtos);
        userWsDto.setMessage("User updated successfully");
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }

    @PostMapping("/user/edit")
    @ResponseBody
    public UserWsDto handleEdit(@RequestBody UserWsDto request) {
        userService.populateCommonFields(request);
        userService.save(request);
        request.setMessage("User updated successfully");
        request.setBaseUrl(ADMIN_USER);
        return request;
    }

    @GetMapping("/user/add")
    @ResponseBody
    public UserWsDto addUser() {
        UserWsDto userWsDto = new UserWsDto();
        userService.populateCommonFields(userWsDto);
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }

    @PostMapping("/user/delete")
    @ResponseBody
    public UserWsDto deleteUser(@RequestBody UserWsDto userWsDto) {
        for (UserDto userDto : userWsDto.getUsers()) {
            userRepository.deleteByIdentifier(userDto.getIdentifier());
        }
        userWsDto.setBaseUrl(ADMIN_USER);
        userWsDto.setMessage("Data deleted successfully!!");
        return userWsDto;
    }

    @PostMapping("/user/upload")
    public UserWsDto uploadFile(@RequestBody MultipartFile file) {
        UserWsDto userWsDto = new UserWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.USER, EntityConstants.USER, userWsDto);
            if (StringUtils.isEmpty(userWsDto.getMessage())) {
                userWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return userWsDto;
    }

    @PostMapping("/user/export")
    @ResponseBody
    public UserWsDto uploadFile(@RequestBody UserWsDto userWsDto) {

        try {
            userWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.USER, userWsDto.getHeaderFields()));
            return userWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
