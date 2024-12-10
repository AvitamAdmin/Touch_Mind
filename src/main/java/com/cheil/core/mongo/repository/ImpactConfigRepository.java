package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.ImpactConfig;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("ImpactConfigRepository")
public interface ImpactConfigRepository extends GenericImportRepository<ImpactConfig> {
    ImpactConfig findByRecordId(String id);

    List<ImpactConfig> findByStatusOrderByIdentifier(boolean status);

    List<ImpactConfig> findAllByOrderByIdentifier();

    void deleteByRecordId(String id);
}
