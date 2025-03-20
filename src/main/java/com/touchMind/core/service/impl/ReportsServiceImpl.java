package com.touchMind.core.service.impl;

import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.Reports;
import com.touchMind.core.mongo.model.ReportsMapper;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.core.mongo.repository.ReportsMapperRepository;
import com.touchMind.core.mongo.repository.ReportsRepository;
import com.touchMind.core.mongotemplate.QATestResult;
import com.touchMind.core.mongotemplate.repository.QARepository;
import com.touchMind.core.service.ReportsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportsServiceImpl implements ReportsService {

    @Autowired
    private ReportsMapperRepository reportsMapperRepository;

    @Autowired
    private QARepository qaRepository;

    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Autowired
    private ReportsRepository reportsRepository;

    @Override
    public void processReport(String subsidiary, String site, String campaign, String sessionId, String testName, String sku) {
        List<ReportsMapper> mappers = reportsMapperRepository.findBySubsidiaryAndSiteAndCampaignOrderByPriorityDesc(subsidiary, site, campaign);
        if (CollectionUtils.isNotEmpty(mappers)) {
            List<QaLocatorResultReport> qaLocatorResultReports = getQaLocatorResultReport(subsidiary, site, campaign, sessionId, testName, sku);
            if (CollectionUtils.isNotEmpty(qaLocatorResultReports)) {
                qaLocatorResultReports.forEach(qaLocatorResultReport -> {
                    mappers.forEach(mapper -> {
                        Pattern pattern = Pattern.compile(mapper.getPattern());
                        Matcher matcher = pattern.matcher(qaLocatorResultReport.getMessage());
                        Reports reports = null;
                        if (matcher.matches()) {
                            Optional<Reports> reportsOptional = reportsRepository.findBySubsidiaryAndSiteAndCampaignAndSessionIdAndTestNameAndSkuAndMethodAndErrorType(subsidiary, site, campaign, sessionId, testName, sku, qaLocatorResultReport.getMethod(), qaLocatorResultReport.getErrorType());
                            if (reportsOptional.isPresent()) {
                                reports = reportsOptional.get();
                                reports.setOccurrenceCount(reports.getOccurrenceCount() + 1);
                            } else {
                                reports = new Reports();
                                reports.setSubsidiary(subsidiary);
                                reports.setSite(site);
                                reports.setCampaign(campaign);
                                reports.setSessionId(sessionId);
                                reports.setTestName(testName);
                                reports.setSku(sku);
                                reports.setMethod(qaLocatorResultReport.getMethod());
                                reports.setErrorType(qaLocatorResultReport.getErrorType());
                                reports.setOccurrenceCount(1);
                                reports.setMessage(qaLocatorResultReport.getMessage());
                                reports.setPriority(mapper.getPriority());
                            }
                            reportsRepository.save(reports);
                        }
                    });
                });
            }
        }
    }

    private List<QaLocatorResultReport> getQaLocatorResultReport(String subsidiary, String site, String campaign, String sessionId, String testName, String sku) {
        Optional<QATestResult> result = qaRepository.findBySubsidiaryAndSiteAndCampaignAndSessionIdAndTestNameAndSku(subsidiary, site, campaign, sessionId, testName, sku);
        return result.map(qaTestResult -> qaLocatorResultReportRepository.findByQaTestResultId(qaTestResult.getId())).orElse(null);
    }
}
