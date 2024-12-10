package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.TestDataType;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestDataTypeRepository")
public interface TestDataTypeRepository extends GenericImportRepository<TestDataType> {
    TestDataType findByRecordId(String recordId);

    List<TestDataType> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String recordId);
}
