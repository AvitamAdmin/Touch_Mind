package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestPlanWsDto;
import com.touchmind.core.mongo.model.TestPlan;
import com.touchmind.form.TestPlanForm;
import org.bson.types.ObjectId;

import java.util.List;

public interface TestPlanService {
    List<TestPlan> findByStatusOrderByIdentifier(Boolean status);

    TestPlanForm editTestPlan(String id);

    boolean addTestPlan(TestPlanForm testPlanForm);

    boolean deleteTestPlan(String id);

    TestPlan getTestPlanByObjectId(ObjectId id);

  //  List<TestPlan> findBySubsidiary(String id);

  //  List<TestPlan> findBySubsidiaryAndStatus(String id, boolean status);

    TestPlan getTestPlanByRecordId(String id);

    List<TestPlan> findAllByOrderByIdentifier();

    TestPlanWsDto handleEdit(TestPlanWsDto request);
}
