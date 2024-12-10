package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SourceTargetMappingRepository")
public interface SourceTargetMappingRepository extends GenericImportRepository<SourceTargetMapping> {
    List<SourceTargetMapping> findByDataRelation(String id);

    SourceTargetMapping findByIdentifier(String sourceTargetId);

    SourceTargetMapping findBySubsidiaries(Subsidiary subsidiary);

    SourceTargetMapping findByNode(String node);

    List<SourceTargetMapping> findBySubsidiariesAndNode(String subsidiary, String node);

    List<SourceTargetMapping> findByStatusOrderByIdentifier(boolean b);

    void deleteByRecordId(String valueOf);

    SourceTargetMapping findByRecordId(String id);

}
