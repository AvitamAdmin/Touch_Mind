package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.OperationCheck;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("OperationCheckRepository")
public interface OperationCheckRepository extends GenericImportRepository<OperationCheck> {
    List<OperationCheck> findByShortcutName(String shortcutName);
}