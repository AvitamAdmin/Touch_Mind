package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestDataSubtype;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestDataSubtypeRepository")
public interface TestDataSubtypeRepository extends GenericImportRepository<TestDataSubtype> {
    List<TestDataSubtype> findByTestDataType(String testDataType);

    TestDataSubtype findByIdentifier(String identifier);

    List<TestDataSubtype> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String identifier);
}
