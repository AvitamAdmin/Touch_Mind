package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocatorGroup;
import com.touchmind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.qa.utils.TestDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        return testLocatorGroupRepository.findByRecordId(objectId);
    }

    @Override
    public LocatorGroupDto editLocatorGroup(String locatorGroupId) {
        TestLocatorGroup locatorGroup = testLocatorGroupRepository.findByRecordId(locatorGroupId);
        if (locatorGroup != null) {
            return modelMapper.map(locatorGroup, LocatorGroupDto.class);
        }
        return null;
    }

    @Override
    public boolean addLocatorGroup(LocatorGroupDto locatorGroup) {
        if (CollectionUtils.isNotEmpty(locatorGroup.getTestLocators())) {
            Set<LocatorPriority> validLocators = locatorGroup.getTestLocators().stream().filter(locatorPriorityForm -> StringUtils.isNotEmpty(locatorPriorityForm.getLocatorId())).collect(Collectors.toSet());
            locatorGroup.setTestLocators(validLocators.stream().collect(Collectors.toList()));
        }
        TestLocatorGroup testLocatorGroup = modelMapper.map(locatorGroup, TestLocatorGroup.class);
        if (locatorGroup.getRecordId() != null) {
            TestLocatorGroup locatorGroupRecord = testLocatorGroupRepository.findByRecordId(locatorGroup.getRecordId());
            if (locatorGroupRecord != null) {
                testLocatorGroup.setId(locatorGroupRecord.getId());
                if (CollectionUtils.isEmpty(locatorGroup.getTestLocators())) {
                    testLocatorGroup.setTestLocators(locatorGroupRecord.getTestLocators());
                }
            }
        }
        testLocatorGroupRepository.save(testLocatorGroup);
        if (StringUtils.isEmpty(testLocatorGroup.getRecordId())) {
            testLocatorGroup.setRecordId(String.valueOf(testLocatorGroup.getId().getTimestamp()));
        }
        testLocatorGroupRepository.save(testLocatorGroup);
        return true;
    }

    @Override
    public void deleteLocatorGroup(String locatorId) {
        testLocatorGroupRepository.deleteByRecordId(locatorId);
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
