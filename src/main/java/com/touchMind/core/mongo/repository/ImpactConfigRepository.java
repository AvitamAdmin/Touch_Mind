package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.ImpactConfig;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("ImpactConfigRepository")
public interface ImpactConfigRepository extends GenericImportRepository<ImpactConfig> {
    ImpactConfig findByIdentifier(String id);

    List<ImpactConfig> findByStatusOrderByIdentifier(boolean status);

    List<ImpactConfig> findAllByOrderByIdentifier();

    void deleteByIdentifier(String id);
}
