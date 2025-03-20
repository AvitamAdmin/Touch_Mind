package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.SystemRole;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SystemRoleRepository")
public interface SystemRoleRepository extends GenericImportRepository<SystemRole> {
    SystemRole findByIdentifier(String identifier);

    List<SystemRole> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String valueOf);
}
