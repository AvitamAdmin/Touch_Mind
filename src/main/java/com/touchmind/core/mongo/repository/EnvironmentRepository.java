package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Environment;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EnvironmentRepository")
public interface EnvironmentRepository extends GenericImportRepository<Environment> {

    Environment findByRecordId(String recordId);

    List<Environment> findByStatusOrderByIdentifier(Boolean status);

    List<Environment> findBySubsidiaries(String subsidiary);


    void deleteByRecordId(String recordId);

}

