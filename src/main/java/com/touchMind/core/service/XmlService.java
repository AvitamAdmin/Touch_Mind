package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.model.DataSource;

import java.util.List;
import java.util.Map;

public interface XmlService {

    Map<String, String> getRelationKeysForDatasource(DataSource dataSource, String node);

    Map<Object, Map<String, Object>> processXml(org.jsoup.nodes.Document doc, ReportDto reportDto, List<String> inputParams);

    boolean processXmlData(org.jsoup.nodes.Document doc, ReportDto reportDto);
}
