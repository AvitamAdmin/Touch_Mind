package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.CronJobProfile;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("CronJobProfileRepository")
public interface CronJobProfileRepository extends GenericImportRepository<CronJobProfile> {
    CronJobProfile findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CronJobProfile> findByStatusOrderByIdentifier(boolean b);

    List<CronJobProfile> findAllByOrderByIdentifier();
}
