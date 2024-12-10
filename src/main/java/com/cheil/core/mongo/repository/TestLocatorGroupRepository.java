package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.TestLocatorGroup;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TestLocatorGroupRepository")
public interface TestLocatorGroupRepository extends GenericImportRepository<TestLocatorGroup> {
    TestLocatorGroup findByIdentifier(String identifier);

    TestLocatorGroup findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    List<TestLocatorGroup> findAllByOrderByIdentifier();

    List<TestLocatorGroup> findByStatusOrderByIdentifier(Boolean status);
}
