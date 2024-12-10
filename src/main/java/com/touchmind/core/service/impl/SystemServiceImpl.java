package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.SystemDto;
import com.touchmind.core.mongo.dto.SystemWsDto;
import com.touchmind.core.mongo.model.System;
import com.touchmind.core.mongo.repository.CatalogRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.SystemRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.SiteService;
import com.touchmind.core.service.SubsidiaryService;
import com.touchmind.core.service.SystemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SubsidiaryService subsidiaryService;

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
            if (system.getRecordId() != null) {
                requestData = systemRepository.findByRecordId(system.getRecordId());
                modelMapper.map(system, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.SYSTEM, system.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(system, System.class);
            }
            baseService.populateCommonData(requestData);
            systemRepository.save(requestData);
            if (system.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            systemRepository.save(requestData);
            systemList.add(requestData);
            systemWsDto.setMessage("System was updated successfully!!");
        }
        systemWsDto.setSystems(modelMapper.map(systemList, List.class));
        return systemWsDto;
    }
}
