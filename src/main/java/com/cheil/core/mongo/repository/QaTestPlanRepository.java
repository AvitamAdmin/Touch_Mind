package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.TestPlan;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("QaTestPlanRepository")
public interface QaTestPlanRepository extends GenericImportRepository<TestPlan> {
    TestPlan findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    List<TestPlan> findBySubsidiary(String id);

    List<TestPlan> findBySubsidiaryAndStatus(String id, boolean status);

    List<TestPlan> findByStatusOrderByIdentifier(Boolean status);

    List<TestPlan> findAllByOrderByIdentifier();

}
