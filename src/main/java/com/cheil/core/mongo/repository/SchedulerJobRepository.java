package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.SchedulerJob;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
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
