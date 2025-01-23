package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.LocatorGroupFailedResult;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("LocatorGroupFailedResultRepository")
public interface LocatorGroupFailedResultRepository extends GenericImportRepository<LocatorGroupFailedResult> {
    LocatorGroupFailedResult findByGroupId(String groupId);

    LocatorGroupFailedResult findBySessionId(String sessionId);
}
