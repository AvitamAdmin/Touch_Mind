package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestPlan;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("QaTestPlanRepository")
public interface QaTestPlanRepository extends GenericImportRepository<TestPlan> {
    TestPlan findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<TestPlan> findBySubsidiary(String id);

    List<TestPlan> findBySubsidiaryAndStatus(String id, boolean status);

    List<TestPlan> findByStatusOrderByIdentifier(Boolean status);

    List<TestPlan> findAllByOrderByIdentifier();

}
