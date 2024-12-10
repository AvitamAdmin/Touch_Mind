package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.TestDataType;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestDataTypeRepository")
public interface TestDataTypeRepository extends GenericImportRepository<TestDataType> {
    TestDataType findByRecordId(String recordId);

    List<TestDataType> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String recordId);
}
