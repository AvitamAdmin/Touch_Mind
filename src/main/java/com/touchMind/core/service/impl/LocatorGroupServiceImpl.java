package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.service.LocatorGroupService;
import com.touchMind.qa.utils.TestDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocatorGroupServiceImpl implements LocatorGroupService {

    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TestLocatorGroup> getLocatorsGroup() {
        return testLocatorGroupRepository.findAll();
    }

    @Override
    public List<TestLocatorGroup> getLocatorsGroupOrderByIdentifier() {
        return testLocatorGroupRepository.findAllByOrderByIdentifier();
    }

    @Override
    public TestLocatorGroup findLocatorByGroupId(String objectId) {
        TestLocatorGroup testLocatorGroupOptional = testLocatorGroupRepository.findByIdentifier(objectId);
        return testLocatorGroupOptional;
    }

    @Override
    public TestLocatorGroupDto editLocatorGroup(String locatorGroupId) {
        TestLocatorGroup locatorGroup = testLocatorGroupRepository.findByIdentifier(locatorGroupId);
        if (locatorGroup != null) {
            return modelMapper.map(locatorGroup, TestLocatorGroupDto.class);
        }
        return null;
    }

    @Override
    public boolean addLocatorGroup(TestLocatorGroupDto locatorGroup) {
        if (CollectionUtils.isNotEmpty(locatorGroup.getTestLocators())) {
            Set<LocatorPriority> validLocators = locatorGroup.getTestLocators().stream().filter(locatorPriorityForm -> StringUtils.isNotEmpty(locatorPriorityForm.getLocatorId())).collect(Collectors.toSet());
            locatorGroup.setTestLocators(validLocators.stream().collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(locatorGroup.getConditionGroupList())) {
            locatorGroup.setConditionGroupList(locatorGroup.getConditionGroupList().stream().filter(conditionGroupForm -> StringUtils.isNotEmpty(conditionGroupForm.getToolkitId())).collect(Collectors.toList()));
        }
        TestLocatorGroup testLocatorGroup = modelMapper.map(locatorGroup, TestLocatorGroup.class);
        TestLocatorGroup locatorGroupRecord = testLocatorGroupRepository.findByIdentifier(locatorGroup.getIdentifier());
        if (locatorGroupRecord != null) {
            testLocatorGroup.setId(locatorGroupRecord.getId());
            if (CollectionUtils.isEmpty(locatorGroup.getTestLocators())) {
                testLocatorGroup.setTestLocators(locatorGroupRecord.getTestLocators());
            }
        }
        testLocatorGroupRepository.save(testLocatorGroup);
        return true;
    }

    @Override
    public void deleteLocatorGroup(String locatorId) {
        testLocatorGroupRepository.deleteByIdentifier(locatorId);
    }

    @Override
    public List<String> getConditionOperators() {
        return List.of(TestDataUtils.Field.EQUALS.toString(),
                TestDataUtils.Field.GREATER_THAN.toString(),
                TestDataUtils.Field.LESS_THAN.toString(),
                TestDataUtils.Field.NOT_NULL.toString(),
                TestDataUtils.Field.NOT_EMPTY.toString()
        );
    }
}
