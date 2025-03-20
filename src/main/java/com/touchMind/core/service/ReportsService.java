package com.touchMind.core.service;

public interface ReportsService {
    void processReport(String subsidiary, String site, String campaign, String sessionId, String testName, String sku);
}
