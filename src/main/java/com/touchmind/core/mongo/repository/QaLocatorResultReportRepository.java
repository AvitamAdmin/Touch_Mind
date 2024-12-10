package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

@Repository("QaLocatorResultReportRepository")
public interface QaLocatorResultReportRepository extends GenericImportRepository<QaLocatorResultReport> {
}
