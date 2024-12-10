package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Environment;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EnvironmentRepository")
public interface EnvironmentRepository extends GenericImportRepository<Environment> {

    Environment findByRecordId(String recordId);

    List<Environment> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String recordId);

}

