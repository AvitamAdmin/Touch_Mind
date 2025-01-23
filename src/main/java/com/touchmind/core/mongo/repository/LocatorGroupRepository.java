package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.LocatorGroup;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("LocatorGroupRepository")
public interface LocatorGroupRepository extends GenericImportRepository<LocatorGroup> {
    LocatorGroup findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    List<LocatorGroup> findByStatusOrderByIdentifier(Boolean status);
}
