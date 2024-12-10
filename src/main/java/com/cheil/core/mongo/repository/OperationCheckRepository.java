package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.OperationCheck;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("OperationCheckRepository")
public interface OperationCheckRepository extends GenericImportRepository<OperationCheck> {
    List<OperationCheck> findByShortcutName(String shortcutName);
}