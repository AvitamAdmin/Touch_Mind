package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.TestDataTypeDto;
import com.cheil.core.mongo.dto.TestDataTypeWsDto;
import com.cheil.core.mongo.model.TestDataType;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestDataTypeRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.SubsidiaryService;
import com.cheil.core.service.TestDataTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestDataTypeServiceImpl implements TestDataTypeService {

    public static final String ADMIN_TEST_DATA_TYPE = "/admin/testdatatype";

    @Autowired
    private SubsidiaryService subsidiaryService;

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
            if (testDataType.getRecordId() != null) {
                requestData = testDataTypeRepository.findByRecordId(testDataType.getRecordId());
                modelMapper.map(testDataType, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_DATA_TYPE, testDataType.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(testDataType, TestDataType.class);
            }
            baseService.populateCommonData(requestData);
            testDataTypeRepository.save(requestData);
            if (requestData.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            testDataTypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
            testDataTypeRepository.save(requestData);
            testDataTypeList.add(requestData);
        }
        testDataTypeWsDto.setTestDataTypes(modelMapper.map(testDataTypeList, List.class));
        return testDataTypeWsDto;
    }
}
