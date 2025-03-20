package com.touchMind.core.mongo.repository;

import com.touchMind.core.mongo.model.Reports;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("ReportsRepository")
public interface ReportsRepository extends GenericImportRepository<Reports> {
    Optional<Reports> findBySubsidiaryAndSiteAndCampaignAndSessionIdAndTestNameAndSkuAndMethodAndErrorType(String subsidiary, String site, String campaign, String sessionId, String testName, String sku, String method, String errorType);
}
