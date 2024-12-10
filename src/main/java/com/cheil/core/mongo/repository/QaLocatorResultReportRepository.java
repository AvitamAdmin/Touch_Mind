package com.cheil.core.mongo.repository;

import com.cheil.core.mongo.model.QaLocatorResultReport;
import com.cheil.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("QaLocatorResultReportRepository")
public interface QaLocatorResultReportRepository extends GenericImportRepository<QaLocatorResultReport> {
}
