package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.TestDataType;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestDataTypeRepository")
public interface TestDataTypeRepository extends GenericImportRepository<TestDataType> {
    TestDataType findByIdentifier(String identifier);

    List<TestDataType> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String identifier);
}
