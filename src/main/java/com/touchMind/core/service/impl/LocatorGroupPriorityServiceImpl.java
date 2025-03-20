package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.LocatorsGroupPriorityDto;
import com.touchMind.core.mongo.dto.LocatorsGroupPriorityWsDto;
import com.touchMind.core.mongo.model.LocatorsGroupPriority;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorsGroupPriorityRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.LocatorGroupPriorityService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocatorGroupPriorityServiceImpl implements LocatorGroupPriorityService {
    public static final String ADMIN_CATEGORY = "/admin/category";
    @Autowired
    private LocatorsGroupPriorityRepository locatorsGroupPriorityRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @Override
    public LocatorsGroupPriorityWsDto handleEdit(LocatorsGroupPriorityWsDto request) {
        LocatorsGroupPriorityWsDto locatorsGroupPriorityWsDto = new LocatorsGroupPriorityWsDto();
        LocatorsGroupPriority requestData;
        List<LocatorsGroupPriorityDto> locatorsGroupPriorityDtos = request.getLocatorsGroupPriorityDtoList();
        List<LocatorsGroupPriority> locatorsGroupPriorities = new ArrayList<>();
        for (LocatorsGroupPriorityDto locatorsGroupPriorityDto : locatorsGroupPriorityDtos) {
            if (locatorsGroupPriorityDto.isAdd() && baseService.validateIdentifier(EntityConstants.LOCATOR_GROUP_PRIORITY, locatorsGroupPriorityDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = locatorsGroupPriorityRepository.findByIdentifier(locatorsGroupPriorityDto.getIdentifier());
            if (requestData != null) {
                modelMapper.map(locatorsGroupPriorityDto, requestData);
            } else {
                requestData = modelMapper.map(locatorsGroupPriorityDto, LocatorsGroupPriority.class);
            }
            baseService.populateCommonData(requestData);
            locatorsGroupPriorityRepository.save(requestData);
            locatorsGroupPriorities.add(requestData);
            locatorsGroupPriorityWsDto.setBaseUrl(ADMIN_CATEGORY);
            locatorsGroupPriorityWsDto.setMessage("Locator priority updated successfully!!");
        }
        Type listType = new TypeToken<List<LocatorsGroupPriorityDto>>() {
        }.getType();
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(locatorsGroupPriorities, listType));
        return locatorsGroupPriorityWsDto;
    }
}
