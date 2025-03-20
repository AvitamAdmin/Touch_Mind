package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestLocatorGroup;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestLocatorGroupRepository")
public interface TestLocatorGroupRepository extends GenericImportRepository<TestLocatorGroup> {
    TestLocatorGroup findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<TestLocatorGroup> findAllByOrderByIdentifier();

    List<TestLocatorGroup> findByStatusOrderByIdentifier(Boolean status);
}
