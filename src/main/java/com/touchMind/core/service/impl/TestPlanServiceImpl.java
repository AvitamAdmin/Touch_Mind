package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.dto.TestPlanDto;
import com.touchMind.core.mongo.dto.TestPlanWsDto;
import com.touchMind.core.mongo.model.TestPlan;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.QaTestPlanRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.CoreService;
import com.touchMind.core.service.TestPlanService;
import com.touchMind.form.TestPlanForm;
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
        TestPlan optionalTestPlan = qaTestPlanRepository.findByIdentifier(id);
        return modelMapper.map(optionalTestPlan, TestPlanForm.class);
    }

    @Override
    public boolean addTestPlan(TestPlanForm testPlanForm) {
        TestPlan testPlan = modelMapper.map(testPlanForm, TestPlan.class);
        if (testPlanForm.getIdentifier() != null) {
            TestPlan testPlanRecord = qaTestPlanRepository.findByIdentifier(testPlanForm.getIdentifier());
            if (testPlanRecord != null) {
                testPlan.setId(testPlanRecord.getId());
            }
        }
        qaTestPlanRepository.save(testPlan);
        if (StringUtils.isEmpty(String.valueOf(testPlan.getIdentifier()))) {
            testPlan.setIdentifier(String.valueOf(testPlan.getId().getTimestamp()));
        }
        qaTestPlanRepository.save(testPlan);
        return true;
    }

    @Override
    public boolean deleteTestPlan(String id) {
        qaTestPlanRepository.deleteByIdentifier(id);
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
    public TestPlan getTestPlanByIdentifier(String id) {
        return qaTestPlanRepository.findByIdentifier(id);
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
            if (testPlanDto.isAdd() && baseService.validateIdentifier(EntityConstants.TEST_PLAN_CONFIGURATION, testPlanDto.getIdentifier()) != null) {
                request.setSuccess(false);
                request.setMessage("Identifier already present");
                return request;
            }
            testPlan = qaTestPlanRepository.findByIdentifier(testPlanDto.getIdentifier());
            if (testPlan != null) {
                modelMapper.map(testPlanDto, testPlan);
            } else {
                testPlan = modelMapper.map(testPlanDto, TestPlan.class);
            }
            baseService.populateCommonData(testPlan);
            qaTestPlanRepository.save(testPlan);
            testPlanWsDto.setBaseUrl(ADMIN_QA);
        }
        testPlanWsDto.setMessage("Test plan updated successfully");
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
