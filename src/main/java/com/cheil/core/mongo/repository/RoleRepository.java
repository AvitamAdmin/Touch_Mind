package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.Role;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("RoleRepository")
public interface RoleRepository extends GenericImportRepository<Role> {
    Role findByIdentifier(String identifier);

    Role findByRecordId(String id);

    void deleteByRecordId(String valueOf);

    List<Role> findByStatusOrderByIdentifier(Boolean status);

    List<Role> findByPublishedOrderByIdentifier(Boolean status);
}
