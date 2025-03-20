package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Environment;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EnvironmentRepository")
public interface EnvironmentRepository extends GenericImportRepository<Environment> {

    Environment findByIdentifier(String identifier);

    List<Environment> findByStatusOrderByIdentifier(Boolean status);

    List<Environment> findBySubsidiaries(String subsidiary);


    void deleteByIdentifier(String identifier);

}

