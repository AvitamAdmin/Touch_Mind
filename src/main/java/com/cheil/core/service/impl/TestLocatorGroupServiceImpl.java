package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.TestLocatorGroupDto;
import com.cheil.core.mongo.dto.TestLocatorGroupWsDto;
import com.cheil.core.mongo.model.TestLocatorGroup;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestLocatorGroupRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.TestLocatorGroupService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public TestLocatorGroupWsDto handleEdit(TestLocatorGroupWsDto request) {
        TestLocatorGroupWsDto testLocatorGroupWsDto = new TestLocatorGroupWsDto();
        TestLocatorGroup requestData = null;
        List<TestLocatorGroup> testLocatorGroupList = new ArrayList<>();
        for (TestLocatorGroupDto testLocatorGroupDto : request.getTestLocatorGroups()) {
            if (testLocatorGroupDto.getRecordId() != null) {
                requestData = testLocatorGroupRepository.findByRecordId(testLocatorGroupDto.getRecordId());
                modelMapper.map(testLocatorGroupDto, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_LOCATOR_GROUP, testLocatorGroupDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(testLocatorGroupDto, TestLocatorGroup.class);
            }
            baseService.populateCommonData(requestData);
            testLocatorGroupRepository.save(requestData);
            if (requestData.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            testLocatorGroupRepository.save(requestData);
            testLocatorGroupList.add(requestData);
            testLocatorGroupWsDto.setBaseUrl(ADMIN_LOCATOR_GROUP);
        }
        testLocatorGroupWsDto.setTestLocatorGroups(modelMapper.map(testLocatorGroupList, List.class));
        return testLocatorGroupWsDto;
    }
}
