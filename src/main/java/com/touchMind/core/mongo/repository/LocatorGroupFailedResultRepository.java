package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.LocatorGroupFailedResult;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("LocatorGroupFailedResultRepository")
public interface LocatorGroupFailedResultRepository extends GenericImportRepository<LocatorGroupFailedResult> {
    LocatorGroupFailedResult findByGroupId(String groupId);

    LocatorGroupFailedResult findBySessionId(String sessionId);

    void deleteByGroupId(String groupId);
}
