package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.mongo.model.SchedulerJob;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("SchedulerJobRepository")
public interface SchedulerJobRepository extends GenericImportRepository<SchedulerJob> {
    SchedulerJob findByRecordId(String valueOf);

    SchedulerJob findByNodePath(String path);

    List<SchedulerJob> findByStatusOrderByIdentifier(Boolean status);

    void deleteByRecordId(String recordId);

    SchedulerJob findById(String objectId);
}
