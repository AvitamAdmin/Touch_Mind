package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.TestProfileDto;
import com.touchmind.core.mongo.dto.TestProfileWsDto;

import com.touchmind.core.mongo.model.TestProfile;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.TestDataTypeRepository;
import com.touchmind.core.mongo.repository.TestLocatorRepository;
import com.touchmind.core.mongo.repository.TestProfileRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.TestProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestProfileServiceImpl implements TestProfileService {

    public static final String ADMIN_PROFILE = "/admin/profile";
    public static final String ENTER_TEXT = "EnterText";
    @Autowired
    private TestProfileRepository testProfileRepository;
    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private BaseService baseService;

    @Autowired
    private TestDataTypeRepository testDataTypeRepository;

//    @Override
//    public List<ProfileLocatorDto> getProfileLocators() {
//        List<ProfileLocatorDto> profileLocators = new ArrayList<>();
//        testLocatorRepository.findByMethodName(ENTER_TEXT).stream().forEach(testLocator -> {
//            profileLocators.add(getProfileLocator(testLocator));
//        });
//        return profileLocators;
//
//    }

//    private ProfileLocatorDto getProfileLocator(TestLocator testLocator) {
//        ProfileLocatorDto profileLocator = new ProfileLocatorDto();
//        profileLocator.setLocatorId(testLocator.getIdentifier());
//        profileLocator.setDescription(testLocator.getDescription());
//        if (StringUtils.isNotEmpty(testLocator.getTestDataType())) {
//            TestDataType testDataType = testDataTypeRepository.findByRecordId(testLocator.getTestDataType());
//            if (testDataType != null) {
//                profileLocator.setTestDataType(testDataType.getIdentifier());
//            }
//        }
//        profileLocator.setInputValue("");
//        return profileLocator;
//    }

    @Override
    public void deleteTestProfile(String id) {
        testProfileRepository.deleteByRecordId(id);
    }

    @Override
    public TestProfileWsDto handleCopy(TestProfileWsDto request) {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        TestProfile testProfile = testProfileRepository.findByRecordId(request.getTestProfiles().get(0).getRecordId());
        TestProfile clonedTestProfile = new TestProfile();
        clonedTestProfile.setCreator(coreService.getCurrentUser().getCreator());
        clonedTestProfile.setCreationTime(new Date());
        clonedTestProfile.setLastModified(new Date());
        testProfileRepository.save(clonedTestProfile);
        modelMapper.map(testProfile, clonedTestProfile);
        clonedTestProfile.setRecordId(String.valueOf(clonedTestProfile.getId().getTimestamp()));
        testProfileRepository.save(clonedTestProfile);
        testProfileWsDto.setMessage("TestProfile copied successfully!!");
        return testProfileWsDto;
    }

    @Override
    public TestProfileWsDto handleEdit(TestProfileWsDto request) {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        TestProfile requestData = null;
        List<TestProfileDto> testProfiles = request.getTestProfiles();
        List<TestProfile> testProfileList = new ArrayList<>();
        for (TestProfileDto testProfile : testProfiles) {
            if (testProfile.getRecordId() != null) {
                requestData = testProfileRepository.findByRecordId(testProfile.getRecordId());
                modelMapper.map(testProfile, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_PROFILE, testProfile.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(testProfile, TestProfile.class);
            }
            baseService.populateCommonData(requestData);
            testProfileRepository.save(requestData);
            if (testProfile.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            testProfileRepository.save(requestData);
            testProfileList.add(requestData);
            testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        }
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileList, List.class));
        return testProfileWsDto;

    }
}
