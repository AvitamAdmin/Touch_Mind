package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.SourceTargetParamMapping;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("SourceTargetParamMappingRepository")
public interface SourceTargetParamMappingRepository extends GenericImportRepository<SourceTargetParamMapping> {
    SourceTargetParamMapping findByRecordId(String recordId);
}
