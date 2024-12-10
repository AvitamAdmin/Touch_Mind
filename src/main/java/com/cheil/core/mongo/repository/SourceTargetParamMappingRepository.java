package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.SourceTargetParamMapping;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("SourceTargetParamMappingRepository")
public interface SourceTargetParamMappingRepository extends GenericImportRepository<SourceTargetParamMapping> {
    SourceTargetParamMapping findByRecordId(String recordId);
}
