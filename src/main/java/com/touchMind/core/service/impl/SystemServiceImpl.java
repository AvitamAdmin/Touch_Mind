package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.SystemDto;
import com.touchMind.core.mongo.dto.SystemWsDto;
import com.touchMind.core.mongo.model.System;
import com.touchMind.core.mongo.repository.CatalogRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.SystemRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.SiteService;
import com.touchMind.core.service.SystemService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemServiceImpl implements SystemService {
//
//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;


    @Override
    public SystemWsDto handleEdit(SystemWsDto request) {
        SystemWsDto systemWsDto = new SystemWsDto();
        List<SystemDto> systems = request.getSystems();
        List<System> systemList = new ArrayList<>();
        System requestData = null;
        for (SystemDto system : systems) {
            if (system.isAdd() && baseService.validateIdentifier(EntityConstants.SYSTEM, system.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = systemRepository.findByIdentifier(system.getIdentifier());
            if (requestData != null) {
                modelMapper.map(system, requestData);
            } else {
                requestData = modelMapper.map(system, System.class);
            }
            baseService.populateCommonData(requestData);
            systemRepository.save(requestData);
            systemList.add(requestData);
            systemWsDto.setMessage("System updated successfully!!");
        }
        Type listType = new TypeToken<List<SystemDto>>() {
        }.getType();
        systemWsDto.setSystems(modelMapper.map(systemList, listType));
        return systemWsDto;
    }
}
