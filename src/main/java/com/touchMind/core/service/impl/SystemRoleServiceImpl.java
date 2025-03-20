package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.SystemRoleDto;
import com.touchMind.core.mongo.dto.SystemRoleWsDto;
import com.touchMind.core.mongo.model.SystemRole;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.mongo.repository.SystemRoleRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SystemRoleService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemRoleServiceImpl implements SystemRoleService {

    public static final String ADMIN_SYSTEMROLE = "/admin/systemrole";

    @Autowired
    private SystemRoleRepository systemRoleRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Autowired
    private SystemRepository systemRepository;

    @Override
    public SystemRoleWsDto handleEdit(SystemRoleWsDto request) {
        SystemRole requestData = null;
        SystemRoleWsDto systemRoleWsDto = new SystemRoleWsDto();
        List<SystemRoleDto> systemRoles = request.getSystemRoles();
        List<SystemRole> systemRoleList = new ArrayList<>();
        for (SystemRoleDto systemRole : systemRoles) {
            if (systemRole.isAdd() && baseService.validateIdentifier(EntityConstants.SYSTEM_ROLE, systemRole.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = systemRoleRepository.findByIdentifier(systemRole.getIdentifier());
            if (requestData != null) {
                modelMapper.map(systemRole, requestData);
            } else {
                requestData = modelMapper.map(systemRole, SystemRole.class);
            }
            baseService.populateCommonData(requestData);
            if (systemRole.getSystem() != null) {
                requestData.setSystem(systemRepository.findByIdentifier(systemRole.getSystem().getIdentifier()));
            }
            systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
            systemRoleRepository.save(requestData);
            systemRoleList.add(requestData);
            systemRoleWsDto.setMessage("SystemRole updated successfully!!");
        }
        Type listType = new TypeToken<List<SystemRoleDto>>() {
        }.getType();
        systemRoleWsDto.setSystemRoles(modelMapper.map(systemRoleList, listType));
        return systemRoleWsDto;
    }
}
