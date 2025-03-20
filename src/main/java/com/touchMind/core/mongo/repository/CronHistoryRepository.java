package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.CronHistory;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("CronHistoryRepository")
public interface CronHistoryRepository extends GenericImportRepository<CronHistory> {
    CronHistory findByIdentifier(String valueOf);

    CronHistory findBySessionId(String currentSessionId);
}
