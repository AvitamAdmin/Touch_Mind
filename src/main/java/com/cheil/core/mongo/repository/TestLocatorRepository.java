package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestLocatorRepository")
public interface TestLocatorRepository extends GenericImportRepository<TestLocator> {
    TestLocator findByRecordId(String recordId);

    List<TestLocator> findByMethodName(String methodName);

    void deleteByRecordId(String recordId);

    List<TestLocator> findAllByOrderByIdentifier();

    List<TestLocator> findByStatusOrderByIdentifier(Boolean status);
}
