package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.SchedulerJob;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SchedulerJobRepository")
public interface SchedulerJobRepository extends GenericImportRepository<SchedulerJob> {
    SchedulerJob findByIdentifier(String valueOf);

    SchedulerJob findByNodePath(String path);

    List<SchedulerJob> findByStatusOrderByIdentifier(Boolean status);

    void deleteByIdentifier(String identifier);

    SchedulerJob findById(String objectId);
}
