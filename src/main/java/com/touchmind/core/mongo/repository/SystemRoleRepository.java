package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.SystemRole;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SystemRoleRepository")
public interface SystemRoleRepository extends GenericImportRepository<SystemRole> {
    SystemRole findByRecordId(String recordId);

    List<SystemRole> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String valueOf);
}
