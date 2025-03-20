package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.ErrorType;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorTypeRepository extends GenericImportRepository<ErrorType> {
    ErrorType findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<ErrorType> findAllByOrderByIdentifier();
}
