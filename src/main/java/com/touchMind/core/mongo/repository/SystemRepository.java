package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.System;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SystemRepository")
public interface SystemRepository extends GenericImportRepository<System> {

    System findByIdentifier(String identifier);

    void deleteByIdentifier(String valueOf);

    System findById(String id);

    List<System> findByStatusOrderByIdentifier(Boolean status);

}
