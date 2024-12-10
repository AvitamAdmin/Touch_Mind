package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.Module;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ModuleRepository")
public interface ModuleRepository extends GenericImportRepository<Module> {
    Module findByRecordId(String id);

    List<Module> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String valueOf);
}
