package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.DataRelation;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DataRelationRepository")
public interface DataRelationRepository extends GenericImportRepository<DataRelation> {
    DataRelation findByIdentifier(String dataRelationId);

    void deleteByIdentifier(String valueOf);

    List<DataRelation> findByStatusOrderByIdentifier(Boolean status);

    List<DataRelation> findAllByOrderByIdentifier(Boolean status);

}
