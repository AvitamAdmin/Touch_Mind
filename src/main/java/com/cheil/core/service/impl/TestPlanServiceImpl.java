package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.TestPlanDto;
import com.cheil.core.mongo.dto.TestPlanWsDto;
import com.cheil.core.mongo.model.TestPlan;
import com.cheil.core.mongo.repository.EntityConstants;
import com.cheil.core.mongo.repository.QaTestPlanRepository;
import com.cheil.core.service.BaseService;
import com.cheil.core.service.CoreService;
import com.cheil.core.service.TestPlanService;
import com.cheil.form.TestPlanForm;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestPlanServiceImpl implements TestPlanService {

    public static final String ADMIN_QA = "/admin/qa";
    @Autowired
    private CoreService coreService;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private BaseService baseService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TestPlan> findByStatusOrderByIdentifier(Boolean status) {
        return qaTestPlanRepository.findByStatusOrderByIdentifier(status);
    }

    @Override
    public TestPlanForm editTestPlan(String id) {
        TestPlan optionalTestPlan = qaTestPlanRepository.findByRecordId(id);
        return modelMapper.map(optionalTestPlan, TestPlanForm.class);
    }

    @Override
    public boolean addTestPlan(TestPlanForm testPlanForm) {
        TestPlan testPlan = modelMapper.map(testPlanForm, TestPlan.class);
        if (testPlanForm.getRecordId() != null) {
            TestPlan testPlanRecord = qaTestPlanRepository.findByRecordId(testPlanForm.getRecordId());
            if (testPlanRecord != null) {
                testPlan.setId(testPlanRecord.getId());
            }
        }
        qaTestPlanRepository.save(testPlan);
        if (StringUtils.isEmpty(testPlan.getRecordId())) {
            testPlan.setRecordId(String.valueOf(testPlan.getId().getTimestamp()));
        }
        qaTestPlanRepository.save(testPlan);
        return true;
    }

    @Override
    public boolean deleteTestPlan(String id) {
        qaTestPlanRepository.deleteByRecordId(id);
        return true;
    }

    @Override
    public TestPlan getTestPlanByObjectId(ObjectId id) {
        Optional<TestPlan> optionalTestPlan = qaTestPlanRepository.findById(id);
        if (optionalTestPlan.isPresent()) {
            return optionalTestPlan.get();
        }
        return null;
    }

    @Override
    public TestPlan getTestPlanByRecordId(String id) {
        return qaTestPlanRepository.findByRecordId(id);
    }

    @Override
    public List<TestPlan> findAllByOrderByIdentifier() {
        return qaTestPlanRepository.findAllByOrderByIdentifier();
    }

    @Override
    public TestPlanWsDto handleEdit(TestPlanWsDto request) {
        TestPlanWsDto testPlanWsDto = new TestPlanWsDto();
        TestPlan testPlan = null;
        List<TestPlanDto> testPlans = request.getTestPlans();
        for (TestPlanDto testPlanDto : testPlans) {
            if (testPlanDto.getRecordId() != null) {
                testPlan = qaTestPlanRepository.findByRecordId(testPlanDto.getRecordId());
                modelMapper.map(testPlanDto, testPlan);
            } else {
                if (baseService.validateIdentifier(EntityConstants.TEST_PLAN_CONFIGURATION, testPlanDto.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                testPlan = modelMapper.map(testPlanDto, TestPlan.class);
            }
            baseService.populateCommonData(testPlan);
            qaTestPlanRepository.save(testPlan);
            if (testPlan.getRecordId() == null) {
                testPlan.setRecordId(String.valueOf(testPlan.getId().getTimestamp()));
            }
            qaTestPlanRepository.save(testPlan);
            testPlanWsDto.setBaseUrl(ADMIN_QA);
        }
        return testPlanWsDto;
    }

    public List<TestPlan> findBySubsidiary(String id) {
        return qaTestPlanRepository.findBySubsidiary(id);
    }

    @Override
    public List<TestPlan> findBySubsidiaryAndStatus(String id, boolean status) {
        return qaTestPlanRepository.findBySubsidiaryAndStatus(id, status);
    }
}
