package com.touchmind.core.mongo.repository;

import com.touchmind.core.mongo.model.QaResultReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface QaResultReportRepository extends MongoRepository<QaResultReport, String> {
    Page<QaResultReport> findByCreationTimeBetween(Date startDate, Date endDate, Pageable pageable);

    Page<QaResultReport> findByTestCaseIdIgnoreCaseLike(String testCaseId, Pageable pageable);

    QaResultReport findBySessionIdAndSkuAndTestCaseId(String sessionId, String sku, String testCaseId);

    Page<QaResultReport> findByCreationTimeBetweenAndTestCaseIdIgnoreCaseLike(Date startDate, Date endDate, String testCaseId, Pageable pageable);

    List<QaResultReport> findByStatusAndCreationTimeBetweenAndTestCaseIdIgnoreCaseLikeOrderByCreationTime(boolean status, Date startDate, Date endDate, String testCaseId);
}
