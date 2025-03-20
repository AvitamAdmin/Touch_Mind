package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestDataSubtypeDto;
import com.touchMind.core.mongo.dto.TestDataSubtypeWsDto;
import com.touchMind.core.mongo.model.TestDataSubtype;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.TestDataSubtypeRepository;
import com.touchMind.core.mongo.repository.TestDataTypeRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestDataSubTypeService;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestDataSubTypeServiceImpl implements TestDataSubTypeService {

    public static final String ADMIN_TEST_DATA_TYPE = "/admin/testdatasubtype";

    @Autowired
    private TestDataSubtypeRepository testDataSubtypeRepository;

    @Autowired
    private TestDataTypeRepository testDataTypeRepository;

//    @Autowired
//    private SubsidiaryService subsidiaryService;

    @Autowired
    private CoreService coreService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseService baseService;

    @Override
    public TestDataSubtypeWsDto handleEdit(TestDataSubtypeWsDto request) {
        TestDataSubtypeWsDto testDataSubtypeWsDto = new TestDataSubtypeWsDto();
        TestDataSubtype requestData = null;
        List<TestDataSubtypeDto> testDataSubtypes = request.getTestDataSubtypes();
        List<TestDataSubtype> testDataSubtypeList = new ArrayList<>();
        for (TestDataSubtypeDto testDataSubtype : testDataSubtypes) {
            if (testDataSubtype.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_DATA_SUB_TYPE, testDataSubtype.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            requestData = testDataSubtypeRepository.findByIdentifier(testDataSubtype.getIdentifier());
            if (requestData != null) {
                modelMapper.map(testDataSubtype, requestData);
            } else {
                requestData = modelMapper.map(testDataSubtype, TestDataSubtype.class);
            }
            baseService.populateCommonData(requestData);
            testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
            testDataSubtypeRepository.save(requestData);
            testDataSubtypeList.add(requestData);
        }
        Type listType = new TypeToken<List<TestDataSubtypeDto>>() {
        }.getType();
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypeList, listType));
        testDataSubtypeWsDto.setMessage("TestData Subtype updated successfully");
        return testDataSubtypeWsDto;
    }
}
