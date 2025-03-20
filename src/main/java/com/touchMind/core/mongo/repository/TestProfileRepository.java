package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestProfile;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestProfileRepository")
public interface TestProfileRepository extends GenericImportRepository<TestProfile> {
    List<TestProfile> findBySubsidiary(String subsidiary);

    TestProfile findByIdentifier(String id);

    List<TestProfile> findBySubsidiaryOrderByIdentifier(String subsidiary, boolean status);

    List<TestProfile> findByStatusOrderByIdentifier(Boolean status);

    List<TestProfile> findAllByOrderByIdentifier();

    void deleteByIdentifier(String identifier);
}
