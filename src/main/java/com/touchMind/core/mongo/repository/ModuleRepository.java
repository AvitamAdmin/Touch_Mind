package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Module;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ModuleRepository")
public interface ModuleRepository extends GenericImportRepository<Module> {
    Module findByIdentifier(String id);

    List<Module> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);
}
