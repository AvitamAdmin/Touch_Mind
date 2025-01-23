package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("QaLocatorResultReportRepository")
public interface QaLocatorResultReportRepository extends GenericImportRepository<QaLocatorResultReport> {
    List<QaLocatorResultReport> findByQaTestResultId(ObjectId id);
}
