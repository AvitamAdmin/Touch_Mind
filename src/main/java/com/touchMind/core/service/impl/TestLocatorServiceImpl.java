package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestLocatorDto;
import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.dto.TestLocatorWsDto;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestLocatorService;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestLocatorServiceImpl implements TestLocatorService {

    public static final String ADMIN_LOCATOR = "/admin/locator";

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private BaseService baseService;
    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    @Override
    public TestLocatorWsDto handleEdit(TestLocatorWsDto request) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        TestLocator requestData = null;
        List<TestLocatorDto> testLocators = request.getTestLocators();
        List<TestLocator> testLocatorList = new ArrayList<>();
        for (TestLocatorDto testLocator : testLocators) {
            if (testLocator.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_LOCATOR, testLocator.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = testLocatorRepository.findByIdentifier(testLocator.getIdentifier());
            if (requestData != null) {
                modelMapper.map(testLocator, requestData);
            } else {
                requestData = modelMapper.map(testLocator, TestLocator.class);
            }
            baseService.populateCommonData(requestData);
            List<TestLocatorGroupDto> testLocatorGroupList = testLocator.getTestLocatorGroups();
            if (null != testLocatorGroupList) {
                testLocatorGroupList.forEach(locatorGroup -> {
                    TestLocatorGroup testLocatorGroup = testLocatorGroupRepository.findByIdentifier(locatorGroup.getIdentifier());
                    List<LocatorPriority> locatorPriorities = testLocatorGroup.getTestLocators();
                    List<LocatorPriority> locatorPriorityList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(locatorPriorities)) {
                        locatorPriorities.forEach(locatorPriority -> {
                            if (testLocator.getIdentifier() != null && testLocator.getIdentifier().equals(locatorPriority.getLocatorId())) {
                                locatorPriority.setErrorMsg(locatorGroup.getErrorMsg());
                                locatorPriorityList.add(locatorPriority);
                            } else {
                                locatorPriorityList.add(locatorPriority);
                            }
                        });
                        testLocatorGroup.setTestLocators(locatorPriorityList);
                        testLocatorGroupRepository.save(testLocatorGroup);
                    }
                });
            }
            testLocatorRepository.save(requestData);
            testLocatorList.add(requestData);
            testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        }
        Type listType = new TypeToken<List<TestLocatorDto>>() {
        }.getType();
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocatorList, listType));
        testLocatorWsDto.setMessage("Locator updated successfully");
        return testLocatorWsDto;
    }
}
