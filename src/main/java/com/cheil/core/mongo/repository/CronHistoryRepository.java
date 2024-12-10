package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.CronHistory;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("CronHistoryRepository")
public interface CronHistoryRepository extends GenericImportRepository<CronHistory> {
    CronHistory findByRecordId(String valueOf);

    CronHistory findBySessionId(String currentSessionId);
}
