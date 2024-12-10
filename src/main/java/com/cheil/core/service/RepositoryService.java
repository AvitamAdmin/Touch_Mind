package com.cheil.core.service;

import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import com.cheil.core.mongo.repository.generic.GenericRepository;

public interface RepositoryService {
    GenericImportRepository getRepositoryForName(String entityName);

    GenericRepository getRepositoryForRelationId(String relationId);

    GenericRepository getRepositoryForNodeId(String nodeId);

    BaseEntity getEntityForNodeId(String nodeId);

    BaseEntity getNewEntityForName(String name);
}
