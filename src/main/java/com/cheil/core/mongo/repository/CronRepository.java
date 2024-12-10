package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.CronJob;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CronRepository")
public interface CronRepository extends GenericImportRepository<CronJob> {
    CronJob findByRecordId(String valueOf);

    CronJob findByIdentifier(String identifier);

    List<CronJob> findByStatusOrderByIdentifier(Boolean status);

    List<CronJob> findByCronProfileId(String cronProfileId);

    List<CronJob> findByCronProfileIdAndJobStatus(String cronProfileId, String jobStatus);

    void deleteByRecordId(String recordId);

}
