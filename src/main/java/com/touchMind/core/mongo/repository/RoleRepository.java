package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Role;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("RoleRepository")
public interface RoleRepository extends GenericImportRepository<Role> {
    Role findByIdentifier(String identifier);

    void deleteByIdentifier(String valueOf);

    List<Role> findByStatusOrderByIdentifier(Boolean status);

    List<Role> findByPublishedOrderByIdentifier(Boolean status);
}
