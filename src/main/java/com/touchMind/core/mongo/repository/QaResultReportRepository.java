package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.QaResultReport;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QaResultReportRepository extends GenericImportRepository<QaResultReport> {
    Page<QaResultReport> findByCreationTimeBetween(Date startDate, Date endDate, Pageable pageable);

    Page<QaResultReport> findByTestCaseIdIgnoreCaseLike(String testCaseId, Pageable pageable);

    QaResultReport findBySessionIdAndSkuAndTestCaseId(String sessionId, String sku, String testCaseId);

    Page<QaResultReport> findByCreationTimeBetweenAndTestCaseIdIgnoreCaseLike(Date startDate, Date endDate, String testCaseId, Pageable pageable);

    List<QaResultReport> findByStatusAndCreationTimeBetweenAndTestCaseIdIgnoreCaseLikeOrderByCreationTime(boolean status, Date startDate, Date endDate, String testCaseId);
}
