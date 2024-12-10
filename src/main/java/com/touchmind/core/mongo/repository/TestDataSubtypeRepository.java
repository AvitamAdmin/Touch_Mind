package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.TestDataSubtype;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestDataSubtypeRepository")
public interface TestDataSubtypeRepository extends GenericImportRepository<TestDataSubtype> {
    List<TestDataSubtype> findByTestDataType(String testDataType);

    TestDataSubtype findByRecordId(String recordId);

    List<TestDataSubtype> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String recordId);
}
