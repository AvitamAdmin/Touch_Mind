package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.LocatorsGroupPriorityDto;
import com.touchmind.core.mongo.dto.LocatorsGroupPriorityWsDto;
import com.touchmind.core.mongo.model.LocatorsGroupPriority;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.LocatorsGroupPriorityRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.LocatorGroupPriorityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        LocatorsGroupPriority requestData = null;
        List<LocatorsGroupPriorityDto> locatorsGroupPriorityDtos = request.getLocatorsGroupPriorityDtoList();
        List<LocatorsGroupPriority> locatorsGroupPriorities = new ArrayList<>();
        for (LocatorsGroupPriorityDto locatorsGroupPriorityDto : locatorsGroupPriorityDtos) {
            if (locatorsGroupPriorityDto.getRecordId() != null) {
                requestData = locatorsGroupPriorityRepository.findByRecordId(locatorsGroupPriorityDto.getRecordId());
                modelMapper.map(locatorsGroupPriorityDto, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.CATEGORY, locatorsGroupPriorityDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(locatorsGroupPriorityDto, LocatorsGroupPriority.class);
            }
            baseService.populateCommonData(requestData);
            locatorsGroupPriorityRepository.save(requestData);
            if (locatorsGroupPriorityDto.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            locatorsGroupPriorityRepository.save(requestData);
            locatorsGroupPriorities.add(requestData);
            locatorsGroupPriorityWsDto.setBaseUrl(ADMIN_CATEGORY);
            locatorsGroupPriorityWsDto.setMessage("Category was updated Successfully!!");
        }
        locatorsGroupPriorityWsDto.setLocatorsGroupPriorityDtoList(modelMapper.map(locatorsGroupPriorities, List.class));
        return locatorsGroupPriorityWsDto;
    }
}
