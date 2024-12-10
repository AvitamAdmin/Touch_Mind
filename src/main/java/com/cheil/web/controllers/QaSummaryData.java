package com.cheil.web.controllers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class QaSummaryData {
    private Set<String> users;
    private Integer totalTestCaseCount;
    private Integer totalSkuCount;
    private Integer totalIssuesCount;
    private Map<String, Integer> skusMap;
    private Map<String, Integer> testCasesMap;
    private Map<String, Map<String, Set<String>>> issuesMap;
    private Map<String, Integer> issuesChartMap;
    private List<DashboardTableData> failedData;
    private List<DashboardTableData> subsidiaryData;
    private Map<String, Map<String, Integer>> issuesSummaryChartMap;
    private List<Map<String, String>> skuErrorMapList;
}
