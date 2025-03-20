package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.LocatorGroupDto;
import com.touchMind.core.mongo.dto.LocatorGroupWsDto;
import com.touchMind.core.mongo.model.LocatorGroup;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorGroupRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.LocatorGroupsService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocatorGroupsServiceImpl implements LocatorGroupsService {

    public static final String ADMIN_LOCATOR_GROUP = "/admin/qalocatorgroup";

    @Autowired
    private BaseService baseService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private LocatorGroupRepository locatorGroupRepository;

    @Autowired
    private TestLocatorRepository testLocatorRepository;

    @Override
    public List<LocatorGroup> findByStatusOrderByIdentifier(Boolean status) {
        return locatorGroupRepository.findByStatusOrderByIdentifier(status);
    }

    @Override
    public LocatorGroupWsDto handleEdit(LocatorGroupWsDto request) {
        LocatorGroup locatorGroup = null;
        List<LocatorGroup> list = new ArrayList<>();
        for (LocatorGroupDto locatorGroupDto : request.getGroupsDtoList()) {
            if (locatorGroupDto.isAdd() && baseService.validateIdentifier(EntityConstants.LOCATOR_GROUP, locatorGroupDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            locatorGroup = locatorGroupRepository.findByIdentifier(locatorGroupDto.getIdentifier());
            if (locatorGroup != null) {
                modelMapper.map(locatorGroupDto, locatorGroup);
            } else {
                locatorGroup = modelMapper.map(locatorGroupDto, LocatorGroup.class);
            }
            baseService.populateCommonData(locatorGroup);
            for (LocatorPriority locatorPriority : locatorGroupDto.getTestLocators()) {
                locatorPriority.setLocatorName(locatorPriority.getLocatorId());
            }
            locatorGroupRepository.save(locatorGroup);
            list.add(locatorGroup);
            request.setBaseUrl(ADMIN_LOCATOR_GROUP);
            request.setMessage("Locator group updated successfully");
        }
        Type listType = new TypeToken<List<LocatorGroupDto>>() {
        }.getType();
        request.setGroupsDtoList(modelMapper.map(list, listType));
        return request;
    }


}
