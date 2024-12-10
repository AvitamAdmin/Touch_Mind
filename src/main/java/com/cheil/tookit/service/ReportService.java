package com.cheil.tookit.service;

import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import com.cheil.core.service.impl.CronService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface ReportService extends CronService {
    List<BaseEntity> getReport(ReportDto reportDto);

    String getCurrentUserSessionId(HttpServletRequest request, String checkType);

    Map<String, List<String>> getHeaders(Node node, String subsidiary, String mapping);

    List<Map<String, Object>> adjustReportForHeaders(List<BaseEntity> documents, List<String> pValues);

    boolean isDocument(Map<String, Object> document);
}
