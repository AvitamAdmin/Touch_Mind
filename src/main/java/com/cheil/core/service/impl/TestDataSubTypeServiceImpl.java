package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.TestDataSubtypeDto;
import com.cheil.core.mongo.dto.TestDataSubtypeWsDto;
import com.cheil.core.mongo.model.TestDataSubtype;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.TestDataSubtypeRepository;
import com.cheil.core.mongo.repository.TestDataTypeRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.SubsidiaryService;
import com.cheil.core.service.TestDataSubTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestDataSubTypeServiceImpl implements TestDataSubTypeService {

    public static final String ADMIN_TEST_DATA_TYPE = "/admin/testdatasubtype";

    @Autowired
    private TestDataSubtypeRepository testDataSubtypeRepository;

    @Autowired
    private TestDataTypeRepository testDataTypeRepository;

    @Autowired
    private SubsidiaryService subsidiaryService;

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
            if (testDataSubtype.getRecordId() != null) {
                requestData = testDataSubtypeRepository.findByRecordId(testDataSubtype.getRecordId());
                modelMapper.map(testDataSubtype, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_DATA_SUB_TYPE, testDataSubtype.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(testDataSubtype, TestDataSubtype.class);
            }
            baseService.populateCommonData(requestData);
            testDataSubtypeRepository.save(requestData);
            if (testDataSubtype.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            testDataSubtypeWsDto.setBaseUrl(ADMIN_TEST_DATA_TYPE);
            testDataSubtypeRepository.save(requestData);
            testDataSubtypeList.add(requestData);
        }
        testDataSubtypeWsDto.setTestDataSubtypes(modelMapper.map(testDataSubtypeList, List.class));
        return testDataSubtypeWsDto;
    }
}
