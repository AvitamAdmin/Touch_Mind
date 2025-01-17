package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.NodeDto;
import com.touchmind.core.mongo.dto.RoleDto;
import com.touchmind.core.mongo.dto.RoleWsDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.Role;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.RoleRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.RoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RoleServiceImpl implements RoleService {
    public static final String ADMIN_ROLE = "/admin/role";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CoreService coreService;

    @Override
    public RoleWsDto handelCopy(RoleWsDto request) {
        RoleWsDto roleWsDto = new RoleWsDto();
        Role role = roleRepository.findByRecordId(request.getRoles().get(0).getRecordId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        roleWsDto.setRoles(List.of(modelMapper.map(role, RoleDto.class)));
        Role clonedRole = new Role();
        clonedRole.setCreationTime(new Date());
        clonedRole.setLastModified(new Date());
        clonedRole.setCreator(principalObject.getUsername());
        roleRepository.save(clonedRole);
        modelMapper.map(role, clonedRole);
        clonedRole.setRecordId(String.valueOf(clonedRole.getId().getTimestamp()));
        roleRepository.save(clonedRole);
        roleWsDto.setBaseUrl(ADMIN_ROLE);
        roleWsDto.setMessage("Role copied successfully!");
        return roleWsDto;
    }

    @Override
    public RoleWsDto handleEdit(RoleWsDto request) {



        RoleWsDto roleWsDto = new RoleWsDto();
        List<RoleDto> roles = request.getRoles();
        List<Role> roleList = new ArrayList<>();
        Role requestData = null;
        for (RoleDto role : roles) {
            String userName = coreService.getCurrentUser().getUsername();
            if (role.getRecordId() != null) {
                requestData = roleRepository.findByRecordId(role.getRecordId());
                modelMapper.map(role, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.ROLE, role.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(role, Role.class);
                requestData.setCreationTime(new Date());
                requestData.setCreator(userName);
            }
            populatePermissions(role, requestData);
            baseService.populateCommonData(requestData);
            roleRepository.save(requestData);
            if (requestData.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            requestData.setLastModified(new Date());
            requestData.setModifiedBy(userName);
            roleRepository.save(requestData);
            roleList.add(requestData);
            roleWsDto.setMessage("Role was updated successfully!");
            roleWsDto.setBaseUrl(ADMIN_ROLE);
        }
        roleWsDto.setRoles(modelMapper.map(roleList, List.class));
        return roleWsDto;
    }

    private void populatePermissions(RoleDto role, Role requestData) {
        if (CollectionUtils.isNotEmpty(role.getPermissions())) {
            Set<Node> permissions = new HashSet<>();
            for (NodeDto permission : role.getPermissions()) {
                permissions.add(nodeRepository.findByRecordId(permission.getRecordId()));
            }
            requestData.setPermissions(permissions);
        }
    }
}
