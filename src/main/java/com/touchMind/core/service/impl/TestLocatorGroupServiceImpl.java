package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.dto.TestLocatorGroupWsDto;
import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.LocatorGroupRepository;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestLocatorGroupService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestLocatorGroupServiceImpl implements TestLocatorGroupService {

    public static final String ADMIN_LOCATOR_GROUP = "/admin/locatorGroup";

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private LocatorGroupRepository locatorGroupRepository;

    @Override
    public TestLocatorGroupWsDto handleEdit(TestLocatorGroupWsDto request) {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        TestLocatorGroup requestData = null;
        List<TestLocatorGroup> testLocatorGroupList = new ArrayList<>();
        for (TestLocatorGroupDto testLocatorGroupDto : request.getTestLocatorGroups()) {
            if (testLocatorGroupDto.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_LOCATOR_GROUP, testLocatorGroupDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = testLocatorGroupRepository.findByIdentifier(testLocatorGroupDto.getIdentifier());
            if (requestData != null) {
                modelMapper.map(testLocatorGroupDto, requestData);
            } else {
                requestData = modelMapper.map(testLocatorGroupDto, TestLocatorGroup.class);
            }
            baseService.populateCommonData(requestData);
            testLocatorGroupRepository.save(requestData);
            testLocatorGroupList.add(requestData);
            testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        }
        Type listType = new TypeToken<List<TestLocatorGroupDto>>() {
        }.getType();
        testLocatorGroupWsDto.setTestLocatorGroups(modelMapper.map(testLocatorGroupList, listType));
        testLocatorGroupWsDto.setMessage("Locator Group updated successfully");
        return testLocatorGroupWsDto;
    }
}
