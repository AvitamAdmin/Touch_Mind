package com.touchmind.core.mongotemplate.repository;

import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository("QARepository")
public interface QARepository extends GenericImportRepository<QATestResult> {

    List<QATestResult> findByCreationTimeBetweenAndDashboard(LocalDateTime startDate, LocalDateTime endDate, String dashBoard);

   // List<QATestResult> findByCreationTimeBetweenAndSubsidiaryEqualsAndDashboard(LocalDateTime startDate, LocalDateTime endDate, String subsidiary, String dashBoard);

    List<QATestResult> findBySessionId(String sessionId);

    List<QATestResult> findBySessionIdAndDashboardOrderByIdDesc(String sessionId, String dashBoard);

    List<QATestResult> findBySessionIdAndLocatorGroupIdentifier(String sessionId, Object locatorGroupIdentifier);

    List<QATestResult> findBySessionIdAndLocatorGroupIdentifierAndTestName(String sessionId, Object locatorGroupIdentifier, String testName);

  //  List<QATestResult> findByCreationTimeBetweenAndSubsidiaryAndUserAndDashboard(LocalDateTime startDate, LocalDateTime endDate, String subsidiary, String user, String dashBoard);

    List<QATestResult> findByCreationTimeBetweenAndUserAndDashboard(LocalDateTime startDate, LocalDateTime endDate, String user, String dashBoard);

    List<QATestResult> findAllByCreationTimeBeforeAndResultStatus(LocalDateTime startDate, int i);

 //    List<QATestResult> findBySubsidiaryAndUserAndDashboardOrderByIdDesc(String subId, String user, String dashBoardId);

    List<QATestResult> findByUserAndDashboardOrderByIdDesc(String user, String dashBoardId);

  //  List<QATestResult> findBySessionIdAndDashboardAndSubsidiaryOrderByIdDesc(String sessionId, String dashBoardId, String subId);

    List<QATestResult> findBySessionIdAndDashboardAndUserOrderByIdDesc(String sessionId, String dashBoardId, String user);

 //   List<QATestResult> findBySessionIdAndDashboardAndSubsidiaryAndUserOrderByIdDesc(String sessionId, String dashBoardId, String subId, String user);

    List<QATestResult> findAllByDashboardOrderByIdDesc(String dashBoardId);

   // List<QATestResult> findBySubsidiaryAndDashboardOrderByIdDesc(String subId, String dashBoardId);

   // List<QATestResult> findBySessionIdAndSubsidiaryAndTestName(String sessionId, String subsidiary, String testPlan);

    void deleteById(String id);
}
