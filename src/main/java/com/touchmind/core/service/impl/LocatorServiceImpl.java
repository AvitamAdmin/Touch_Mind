package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.dto.TestLocatorDto;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.model.TestLocatorGroup;
import com.touchmind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchmind.core.mongo.repository.TestLocatorRepository;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.core.service.LocatorService;
import com.touchmind.core.service.SiteService;
import com.touchmind.qa.strategies.ActionType;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class LocatorServiceImpl implements LocatorService {

    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SiteService siteService;

    @Autowired
    private LocatorGroupService locatorGroupService;

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    private static void populateExistingData(TestLocatorDto locator, TestLocator testLocator, TestLocator testLocatorRecord) {
        testLocator.setId(testLocatorRecord.getId());
        //testLocator.setTestDataType(testLocatorRecord.getTestDataType());
        //testLocator.setTestDataSubtype(testLocatorRecord.getTestDataSubtype());
        if (null == locator.getUiLocatorSelector()) {
            testLocator.setUiLocatorSelector(testLocatorRecord.getUiLocatorSelector());
        }
    }

    @Override
    public List<TestLocator> getLocators() {
        return testLocatorRepository.findAllByOrderByIdentifier();
    }

    @Override
    public TestLocatorDto editLocator(String locatorId) {
        TestLocator testLocator = testLocatorRepository.findByRecordId(locatorId);
        if (testLocator != null) {
            TestLocatorDto locatorForm = modelMapper.map(testLocator, TestLocatorDto.class);
            //TODO check if this is correctly fetched
            //locatorForm.setTestLocatorGroups(getLocatorGroups(String.valueOf(locatorForm.getId())));
            return locatorForm;
        }
        return null;
    }

    @Override
    public boolean addLocator(TestLocatorDto locator) {
        TestLocator testLocator = modelMapper.map(locator, TestLocator.class);
        if (locator.getRecordId() != null) {
            TestLocator testLocatorRecord = testLocatorRepository.findByRecordId(locator.getRecordId());
            if (testLocatorRecord != null) {
                populateExistingData(locator, testLocator, testLocatorRecord);
            }
        }
        testLocatorRepository.save(testLocator);
        if (StringUtils.isEmpty(String.valueOf(testLocator.getRecordId()))) {
            testLocator.setRecordId(String.valueOf(testLocator.getId().getTimestamp()));
        }
        testLocatorRepository.save(testLocator);
        List<LocatorGroupDto> testLocatorGroupList = null;
        //List<LocatorGroupDto> testLocatorGroupList = locator.getTestLocatorGroups();
        if (null != testLocatorGroupList) {
            testLocatorGroupList.forEach(locatorGroup -> {
                if (locatorGroup != null) {
                    List<LocatorPriority> locatorPriorityList = new ArrayList<>();
                    TestLocatorGroup testLocatorGroup = testLocatorGroupRepository.findByIdentifier(locatorGroup.getIdentifier());
                    List<LocatorPriority> locatorPriorities = testLocatorGroup.getTestLocators();
                    locatorPriorities.forEach(locatorPriority -> {
                        if (testLocator.getId() != null && testLocator.getId().toString().equals(locatorPriority.getLocatorId())) {
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
        return true;
    }

    @Override
    public void deleteLocator(String locatorId) {
        testLocatorRepository.deleteByRecordId(locatorId);
    }

    @Override
    public TestLocator getLocatorById(String locatorId) {
        return testLocatorRepository.findByRecordId(locatorId);
    }

    @Override
    public List<String> getMethodNames() {
        List<String> methodNames = new ArrayList<>();
        Class actionTypeClass = ActionType.class;
        Field[] fields = actionTypeClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                methodNames.add(String.valueOf(fields[i].get(fields[i])));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return methodNames;
    }

    @Override
    public List<LocatorGroupDto> getLocatorGroups(String locatorIdentifier) {
        List<LocatorGroupDto> locatorGroupsList = new ArrayList<>();
        List<TestLocatorGroup> locatorGroups = locatorGroupService.getLocatorsGroup();
        locatorGroups.stream().forEach(locatorGroup -> {
            List<LocatorPriority> locatorPriorities = locatorGroup.getTestLocators();
            if (null != locatorPriorities) {
                locatorPriorities.stream().forEach(locatorPriority -> {
                    //TODO check if this works
                    if (StringUtils.isNotEmpty(locatorPriority.getLocatorId()) && locatorPriority.getLocatorId().equals(locatorIdentifier)) {
                        LocatorGroupDto locatorGroupDto = modelMapper.map(locatorGroup, LocatorGroupDto.class);
                        locatorGroupDto.setErrorMsg(locatorPriority.getErrorMsg());
                        locatorGroupsList.add(locatorGroupDto);
                    }
                });
            }
        });
        return locatorGroupsList;
    }

    @Override
    public Map<String, Long> getLocatorGroups() {
        Map<String, Long> locatorGroupsWithPriority = new HashMap<>();
        List<TestLocatorGroup> locatorGroups = locatorGroupService.getLocatorsGroup();
        locatorGroups.stream().forEach(locatorGroup -> {
            locatorGroupsWithPriority.put(locatorGroup.getIdentifier(), null);
        });
        return locatorGroupsWithPriority;
    }

}
