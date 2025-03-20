package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.CronJob;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CronRepository")
public interface CronRepository extends GenericImportRepository<CronJob> {
    CronJob findByIdentifier(String valueOf);

    List<CronJob> findByStatusOrderByIdentifier(Boolean status);

    List<CronJob> findByCronProfileId(String cronProfileId);

    List<CronJob> findByCronProfileIdAndJobStatus(String cronProfileId, String jobStatus);

    void deleteByIdentifier(String identifier);

    List<CronJob> findAllByOrderByIdentifier();

}
