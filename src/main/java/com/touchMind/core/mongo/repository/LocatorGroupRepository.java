package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.LocatorGroup;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("LocatorGroupRepository")
public interface LocatorGroupRepository extends GenericImportRepository<LocatorGroup> {
    LocatorGroup findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<LocatorGroup> findByStatusOrderByIdentifier(Boolean status);
}
