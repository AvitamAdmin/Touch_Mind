package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.ProfileLocatorDto;
import com.touchMind.core.mongo.dto.TestProfileDto;
import com.touchMind.core.mongo.dto.TestProfileWsDto;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.model.TestProfile;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestDataTypeRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.core.mongo.repository.TestProfileRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestProfileService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    @Override
    public List<ProfileLocatorDto> getProfileLocators() {
        List<ProfileLocatorDto> profileLocators = new ArrayList<>();
        testLocatorRepository.findByMethodName(ENTER_TEXT).stream().forEach(testLocator -> {
            profileLocators.add(getProfileLocator(testLocator));
        });
        return profileLocators;

    }

    private ProfileLocatorDto getProfileLocator(TestLocator testLocator) {
        ProfileLocatorDto profileLocator = new ProfileLocatorDto();
        profileLocator.setLocatorId(testLocator.getIdentifier());
        profileLocator.setDescription(testLocator.getShortDescription());
        profileLocator.setInputValue("");
        return profileLocator;
    }

    @Override
    public void deleteTestProfile(String id) {
        testProfileRepository.deleteByIdentifier(id);
    }

    @Override
    public TestProfileWsDto handleCopy(String recordId) {
        TestProfileWsDto testProfileWsDto = new TestProfileWsDto();
        TestProfile testProfile = testProfileRepository.findByIdentifier(recordId);
        TestProfile clonedTestProfile = new TestProfile();
        clonedTestProfile.setCreator(coreService.getCurrentUser().getCreator());
        clonedTestProfile.setCreationTime(new Date());
        clonedTestProfile.setLastModified(new Date());
        testProfileRepository.save(clonedTestProfile);
        modelMapper.map(testProfile, clonedTestProfile);
        String id = String.valueOf(clonedTestProfile.getId().getTimestamp());
        if (testProfileRepository.findByIdentifier(id) != null) {
            id = id + new Random().nextInt(24565);
        }
        clonedTestProfile.setIdentifier(id);
        clonedTestProfile.setIdentifier("Copy_" + id + testProfile.getIdentifier());
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
            if (testProfile.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_PROFILE, testProfile.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = testProfileRepository.findByIdentifier(testProfile.getIdentifier());
            if (requestData != null) {
                modelMapper.map(testProfile, requestData);
            } else {
                requestData = modelMapper.map(testProfile, TestProfile.class);
            }
            baseService.populateCommonData(requestData);
            testProfileRepository.save(requestData);
            testProfileList.add(requestData);
            testProfileWsDto.setBaseUrl(ADMIN_PROFILE);
        }
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        testProfileWsDto.setTestProfiles(modelMapper.map(testProfileList, listType));
        testProfileWsDto.setMessage("Test Profile updated successfully");
        return testProfileWsDto;

    }
}
