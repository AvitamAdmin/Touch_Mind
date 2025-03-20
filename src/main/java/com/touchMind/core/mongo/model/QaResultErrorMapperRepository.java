package com.touchMind.core.mongo.model;

import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaResultErrorMapperRepository extends GenericImportRepository<QaResultErrorMapper> {
    QaResultErrorMapper findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<QaResultErrorMapper> findAllByOrderByIdentifier();
}
