package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.SourceTargetParamMapping;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("SourceTargetParamMappingRepository")
public interface SourceTargetParamMappingRepository extends GenericImportRepository<SourceTargetParamMapping> {
    SourceTargetParamMapping findByIdentifier(String identifier);
}
