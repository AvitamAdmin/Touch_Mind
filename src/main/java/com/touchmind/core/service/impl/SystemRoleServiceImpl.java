package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SystemRoleDto;
import com.touchmind.core.mongo.dto.SystemRoleWsDto;
import com.touchmind.core.mongo.model.SystemRole;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.mongo.repository.SystemRoleRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.SystemRoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (systemRole.getRecordId() != null) {
                requestData = systemRoleRepository.findByRecordId(systemRole.getRecordId());
                modelMapper.map(systemRole, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SYSTEM_ROLE, systemRole.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(systemRole, SystemRole.class);
            }
            baseService.populateCommonData(requestData);
            if (systemRole.getSystem() != null) {
                requestData.setSystem(systemRepository.findByRecordId(systemRole.getSystem().getRecordId()));
            }
            systemRoleRepository.save(requestData);
            if (systemRole.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            systemRoleWsDto.setBaseUrl(ADMIN_SYSTEMROLE);
            systemRoleRepository.save(requestData);
            systemRoleList.add(requestData);
            systemRoleWsDto.setMessage("SystemRole was updated successfully!!");
        }
        systemRoleWsDto.setSystemRoles(modelMapper.map(systemRoleList, List.class));
        return systemRoleWsDto;
    }
}
