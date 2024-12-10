package com.touchmind.core.service;

import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.mongo.repository.generic.GenericRepository;

public interface RepositoryService {
    GenericImportRepository getRepositoryForName(String entityName);

    GenericRepository getRepositoryForRelationId(String relationId);

    GenericRepository getRepositoryForNodeId(String nodeId);

    BaseEntity getEntityForNodeId(String nodeId);

    BaseEntity getNewEntityForName(String name);
}
