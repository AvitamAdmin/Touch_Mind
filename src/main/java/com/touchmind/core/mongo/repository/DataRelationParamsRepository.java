package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.DataRelation;
import com.touchmind.core.mongo.model.DataRelationParams;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataRelationParamsRepository")
public interface DataRelationParamsRepository extends GenericImportRepository<DataRelationParams> {

    List<DataRelationParams> findByDataRelation(DataRelation id);

    DataRelationParams findByRecordId(String recordId);
}
