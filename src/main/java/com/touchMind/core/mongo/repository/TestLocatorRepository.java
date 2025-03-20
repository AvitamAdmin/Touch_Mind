package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestLocatorRepository")
public interface TestLocatorRepository extends GenericImportRepository<TestLocator> {
    TestLocator findByIdentifier(String identifier);

    List<TestLocator> findByMethodName(String methodName);

    void deleteByIdentifier(String identifier);

    List<TestLocator> findAllByOrderByIdentifier();

    List<TestLocator> findByStatusOrderByIdentifier(Boolean status);
}
