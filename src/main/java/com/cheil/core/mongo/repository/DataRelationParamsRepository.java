package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.DataRelation;
import com.cheil.core.mongo.model.DataRelationParams;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataRelationParamsRepository")
public interface DataRelationParamsRepository extends GenericImportRepository<DataRelationParams> {

    List<DataRelationParams> findByDataRelation(DataRelation id);

    DataRelationParams findByRecordId(String recordId);
}
