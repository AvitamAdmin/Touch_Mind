package com.touchmind.web.controllers;

import com.touchmind.core.mongo.dto.UserDto;
import com.touchmind.core.mongo.dto.UserWsDto;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.model.User;
import com.touchmind.core.mongo.repository.UserRepository;
import com.touchmind.core.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
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
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/user")
    @ResponseBody
    public UserWsDto getAllUsers(@RequestBody UserWsDto userWsDto) {
        Pageable pageable = getPageable(userWsDto.getPage(), userWsDto.getSizePerPage(), userWsDto.getSortDirection(), userWsDto.getSortField());
        UserDto userDto = CollectionUtils.isNotEmpty(userWsDto.getUsers()) ? userWsDto.getUsers().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(userDto, userWsDto.getOperator());
        User user = userDto != null ? modelMapper.map(userDto, User.class) : null;
        Page<User> page = isSearchActive(user) != null ? userRepository.findAll(Example.of(user, exampleMatcher), pageable) : userRepository.findAll(pageable);
        userWsDto.setUsers(modelMapper.map(page.getContent(), List.class));
        userWsDto.setTotalPages(page.getTotalPages());
        userWsDto.setTotalRecords(page.getTotalElements());
       // userWsDto.setAttributeList(getConfiguredAttributes(userWsDto.getNode()));
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }

//    @GetMapping("/user/getAdvancedSearch")
//    @ResponseBody
//    public List<SearchDto> getSearchAttributes() {
//        return getGroupedParentAndChildAttributes(new User());
//    }


//    @PostMapping("/user/get")
//    @ResponseBody
//    public UserDto getUser(@RequestBody UserDto userDto) {
//        User user = userRepository.findByUsername(userDto.getUsername());
//        Set<String> roles = new HashSet<>();
//        Set<String> subs = new HashSet<>();
//        UserDto userDto1 = modelMapper.map(user, UserDto.class);
//        if (CollectionUtils.isNotEmpty(user.getRoles())) {
//            for (Role role : user.getRoles()) {
//                roles.add(role.getRecordId());
//            }
//        }
//        if (CollectionUtils.isNotEmpty(user.getSubsidiaries())) {
//            for (Subsidiary subsidiary : user.getSubsidiaries()) {
//                subs.add(subsidiary.getRecordId());
//            }
//        }
//        userDto1.setSubsidiaries(subs);
//        userDto1.setRoles(roles);
//        return userDto1;
//    }

    @PostMapping("/user/getedits")
    @ResponseBody
    public UserWsDto editUser(@RequestBody UserWsDto request) {
        UserWsDto userWsDto = new UserWsDto();
        List<UserDto> userDtos = new ArrayList<>();
        userService.populateCommonFields(userWsDto);
        for (UserDto userDto : request.getUsers()) {
            User user = userRepository.findByRecordId(userDto.getRecordId());
            Set<String> roles = new HashSet<>();
            Set<String> subs = new HashSet<>();
            UserDto userDto1 = modelMapper.map(user, UserDto.class);
            if (CollectionUtils.isNotEmpty(user.getRoles())) {
                for (Role role : user.getRoles()) {
                    roles.add(role.getRecordId());
                }
            }
//            if (CollectionUtils.isNotEmpty(user.getSubsidiaries())) {
//                for (Subsidiary subsidiary : user.getSubsidiaries()) {
//                    subs.add(subsidiary.getRecordId());
//                }
//            }
            userDto1.setRoles(roles);
            userDto1.setSubsidiaries(subs);
            userDtos.add(userDto1);
        }
        userWsDto.setUsers(userDtos);
        userWsDto.setBaseUrl(ADMIN_USER);
        return userWsDto;
    }

    @PostMapping("/user/edit")
    @ResponseBody
    public UserWsDto handleEdit(@RequestBody UserWsDto request) {
        userService.populateCommonFields(request);
        userService.save(request);
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
            userRepository.deleteByRecordId(userDto.getRecordId());
        }
        userWsDto.setBaseUrl(ADMIN_USER);
        userWsDto.setMessage("Deleted Successfully");
        return userWsDto;
    }

//    @PostMapping("/user/upload")
//    public UserWsDto uploadFile(@RequestBody MultipartFile file) {
//        UserWsDto userWsDto = new UserWsDto();
//        try {
//            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.USER, EntityConstants.USER, userWsDto);
//            if (StringUtils.isEmpty(userWsDto.getMessage())) {
//                userWsDto.setMessage("File uploaded successfully!!");
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        return userWsDto;
//    }
//
//    @GetMapping("/user/export")
//    @ResponseBody
//    public UserWsDto uploadFile() {
//        UserWsDto userWsDto = new UserWsDto();
//        try {
//            userWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.USER));
//            return userWsDto;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }
}
