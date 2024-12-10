package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.TestLocatorDto;
import com.cheil.core.mongo.dto.TestLocatorWsDto;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestLocatorRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.TestLocatorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestLoactorServiceImpl implements TestLocatorService {

    public static final String ADMIN_LOCATOR = "/admin/locator";

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private BaseService baseService;

    @Override
    public TestLocatorWsDto handleEdit(TestLocatorWsDto request) {
        TestLocatorWsDto testLocatorWsDto = new TestLocatorWsDto();
        TestLocator requestData = null;
        List<TestLocatorDto> testLocators = request.getTestLocators();
        List<TestLocator> testLocatorList = new ArrayList<>();
        for (TestLocatorDto testLocator : testLocators) {
            if (testLocator.getRecordId() != null) {
                requestData = testLocatorRepository.findByRecordId(testLocator.getRecordId());
                modelMapper.map(testLocator, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_LOCATOR, testLocator.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(testLocator, TestLocator.class);
            }
            baseService.populateCommonData(requestData);
            testLocatorRepository.save(requestData);
            if (testLocator.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            testLocatorRepository.save(requestData);
            testLocatorList.add(requestData);
            testLocatorWsDto.setBaseUrl(ADMIN_LOCATOR);
        }
        testLocatorWsDto.setTestLocators(modelMapper.map(testLocatorList, List.class));
        return testLocatorWsDto;
    }
}
