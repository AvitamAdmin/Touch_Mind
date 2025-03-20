package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestDataTypeDto;
import com.touchMind.core.mongo.dto.TestDataTypeWsDto;
import com.touchMind.core.mongo.model.TestDataType;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestDataTypeRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestDataTypeService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestDataTypeServiceImpl implements TestDataTypeService {

    public static final String ADMIN_TEST_DATA_TYPE = "/admin/testdatatype";

//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private TestDataTypeRepository testDataTypeRepository;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public TestDataTypeWsDto handleEdit(TestDataTypeWsDto request) {
        TestDataTypeWsDto testDataTypeWsDto = new TestDataTypeWsDto();
        List<TestDataTypeDto> testDataTypes = request.getTestDataTypes();
        List<TestDataType> testDataTypeList = new ArrayList<>();
        TestDataType requestData = null;
        for (TestDataTypeDto testDataType : testDataTypes) {
            if (testDataType.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_DATA_TYPE, testDataType.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = testDataTypeRepository.findByIdentifier(testDataType.getIdentifier());
            if (requestData != null) {
                modelMapper.map(testDataType, requestData);
            } else {
                requestData = modelMapper.map(testDataType, TestDataType.class);
            }
            baseService.populateCommonData(requestData);
            testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
            testDataTypeRepository.save(requestData);
            testDataTypeList.add(requestData);
        }
        Type listType = new TypeToken<List<TestDataTypeDto>>() {
        }.getType();
        testDataTypeWsDto.setTestDataTypes(modelMapper.map(testDataTypeList, listType));
        testDataTypeWsDto.setMessage("TestData type updated successfully");

        return testDataTypeWsDto;
    }
}
