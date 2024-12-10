package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.DataRelation;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataRelationRepository")
public interface DataRelationRepository extends GenericImportRepository<DataRelation> {
    DataRelation findByIdentifier(String dataRelationId);

    void deleteByRecordId(String valueOf);

    DataRelation findByRecordId(String id);

    List<DataRelation> findByStatusOrderByIdentifier(Boolean status);

    List<DataRelation> findAllByOrderByIdentifier(Boolean status);

}
