package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.System;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SystemRepository")
public interface SystemRepository extends GenericImportRepository<System> {

    System findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

    System findById(String id);

    List<System> findByStatusOrderByIdentifier(Boolean status);

}
