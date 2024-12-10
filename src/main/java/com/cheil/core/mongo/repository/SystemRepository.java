package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.System;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SystemRepository")
public interface SystemRepository extends GenericImportRepository<System> {

    System findByRecordId(String recordId);

    void deleteByRecordId(String valueOf);

    System findById(String id);

    List<System> findByStatusOrderByIdentifier(Boolean status);

}
