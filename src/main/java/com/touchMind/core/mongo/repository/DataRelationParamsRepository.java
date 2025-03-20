package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DataRelation;
import com.touchMind.core.mongo.model.DataRelationParams;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataRelationParamsRepository")
public interface DataRelationParamsRepository extends GenericImportRepository<DataRelationParams> {

    List<DataRelationParams> findByDataRelation(DataRelation id);

    DataRelationParams findByIdentifier(String identifier);
}
