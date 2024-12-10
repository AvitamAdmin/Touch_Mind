package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.CronJobProfile;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CronJobProfileRepository")
public interface CronJobProfileRepository extends GenericImportRepository<CronJobProfile> {
    CronJobProfile findByIdentifier(String identifier);

    CronJobProfile findByRecordId(String recordId);

    void deleteByRecordId(String recordId);

    List<CronJobProfile> findByStatusOrderByIdentifier(boolean b);

    List<CronJobProfile> findAllByOrderByIdentifier();
}
