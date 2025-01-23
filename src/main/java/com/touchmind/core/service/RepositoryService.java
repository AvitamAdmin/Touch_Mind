package com.touchmind.core.service;

import com.touchmind.core.mongo.repository.generic.GenericImportRepository;

public interface RepositoryService {
    GenericImportRepository getRepositoryForName(String entityName);
}
