package com.touchmind.core.service.impl;

import com.touchmind.core.mongo.dto.DashboardDto;
import com.touchmind.core.mongo.dto.DashboardLabelDto;
import com.touchmind.core.mongo.dto.DashboardWsDto;
import com.touchmind.core.mongo.dto.ImpactLabelDto;
import com.touchmind.core.mongo.model.Dashboard;
import com.touchmind.core.mongo.model.DashboardProfile;
import com.touchmind.core.mongo.model.ImpactConfig;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.repository.DashboardRepository;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.ImpactConfigRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.DashboardProfileService;
import com.touchmind.core.service.DashboardService;
import com.touchmind.form.DashboardForm;
import com.touchmind.web.controllers.DashboardTableData;
import com.touchmind.web.controllers.QaSummaryData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    public static final String ADMIN_DASHBOARD = "/admin/dashboard";

    public static final String VALIDATION = "Validation";
    public static final String BUYING_CONFIGURATOR = "Buying configurator";
    public static final String SUCCESS = "Passed";
    public static final String FAILED = "Failed";
    public static final String PARTIALLY_PASSED = "Partially passed";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CoreService coreService;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private QARepository qaRepository;
    @Autowired
    private DashboardProfileService dashboardProfileService;
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private ImpactConfigRepository impactConfigRepository;
    @Autowired
    private BaseService baseService;

    private static void populateIssueChartData(QaSummaryData qaSummaryData, Map<String, Map<String, Set<String>>> issuesMap, Map<String, Integer> issuesChartMap) {
        double totalCount = 0;
        for (Map<String, Set<String>> valueMap : issuesMap.values()) {
            for (Set<String> value : valueMap.values()) {
                totalCount = totalCount + value.size();
            }
        }
        double finalTotalCount = totalCount;
        for (Map<String, Set<String>> childMap : issuesMap.values()) {
            childMap.keySet().forEach(childKey -> {
                double count = childMap.get(childKey).stream().count();
                Double percentage = count / finalTotalCount * 100;
                issuesChartMap.put(childKey, percentage.intValue());
            });
        }
        qaSummaryData.setIssuesChartMap(issuesChartMap);
        qaSummaryData.setTotalIssuesCount(issuesChartMap.size());
    }

    public Dashboard getDashboardByRecordId(String id) {
        return dashboardRepository.findByRecordId(id);
    }

    public List<Dashboard> getAllDashBoard() {
        return dashboardRepository.findAll();
    }

    public Dashboard getByNode(String node) {
        return dashboardRepository.findByNode(node);
    }

    @Override
    public DashboardForm edit(DashboardForm dashboardForm) {
        Dashboard dashboard = modelMapper.map(dashboardForm, Dashboard.class);
        if (dashboard.getRecordId() != null) {
            Dashboard dashboardRec = dashboardRepository.findByRecordId(dashboard.getRecordId());
            if (dashboardRec != null) {
                ObjectId id = dashboardRec.getId();
                modelMapper.map(dashboard, dashboardRec);
                dashboardRec.setId(id);
                dashboardRepository.save(dashboardRec);
            }
        } else {
            dashboardRepository.save(dashboard);
        }

        if (dashboard.getRecordId() == null) {
            dashboard.setRecordId(String.valueOf(dashboard.getId().getTimestamp()));
            dashboardRepository.save(dashboard);
        }
        return dashboardForm;
    }

    /**
     * Returns the dashboard
     *
     * @return
     */
    @Override
    public QaSummaryData getDashBoard(Dashboard dashboard, String subsidiaryId, String days, String runner) {
        QaSummaryData qaSummaryData = new QaSummaryData();
        getTestResultForLastNDays(dashboard, qaSummaryData, subsidiaryId, days, runner);
        populateLast7DaysIssuesData(qaSummaryData, dashboard, subsidiaryId);
        return qaSummaryData;
    }

    @Override
    public DashboardWsDto handleEdit(DashboardWsDto request) {
        DashboardWsDto dashboardWsDto = new DashboardWsDto();
        List<DashboardDto> dashboards = request.getDashboards();
        List<Dashboard> dashboardList = new ArrayList<>();
        Dashboard requestData = null;
        for (DashboardDto dashboard : dashboards) {
            if (dashboard.getRecordId() != null) {
                requestData = dashboardRepository.findByRecordId(dashboard.getRecordId());
                modelMapper.map(dashboard, requestData);
            } else {
                if (baseService.validateIdentifier(EntityConstants.DASHBOARD, dashboard.getIdentifier()) != null) {
                    request.setSuccess(false);
                    request.setMessage("Identifier already present");
                    return request;
                }
                requestData = modelMapper.map(dashboard, Dashboard.class);

            }
            baseService.populateCommonData(requestData);
            dashboardRepository.save(requestData);
            if (dashboard.getRecordId() == null) {
                requestData.setRecordId(String.valueOf(requestData.getId().getTimestamp()));
            }
            dashboardWsDto.setBaseUrl(ADMIN_DASHBOARD);
            dashboardRepository.save(requestData);
            dashboardList.add(requestData);
        }
        dashboardWsDto.setDashboards(modelMapper.map(dashboardList, List.class));
        return dashboardWsDto;
    }

    /**
     * This method prepare the dashboard for last N days of test runs
     *
     * @param qaSummaryData
     * @param days
     * @param runner
     */
    public void getTestResultForLastNDays(Dashboard dashboard, QaSummaryData qaSummaryData, String subsidiaryId, String days, String runner) {
        List<QATestResult> qaTestResultList = new ArrayList<>();
        List<DashboardTableData> dashboardTableDataList = new ArrayList<>();
        Set<DashboardTableData> subsidiaryDataList = new HashSet<>();
        populateTestResults(subsidiaryId, days, runner, qaTestResultList, dashboard);
        Map<String, Map<String, Set<String>>> issuesMap = new HashMap<>();
        List<Map<String, String>> skuErrorMapList = new ArrayList<>();
        Map<String, Integer> issuesChartMap = new HashMap<>();
        Map<String, Integer> testCasesMap = new HashMap<>();
        Map<String, Integer> skusMap = new HashMap<>();
        int totalPassedCount = 0;
        int totalFailedCount = 0;
        for (QATestResult testResult : qaTestResultList) {
            int passedCount = testResult.getTestPassedCount();
            int failedCount = testResult.getTestFailedCount();
            int status = testResult.getResultStatus();
            int totalCount = passedCount + failedCount;
            String subId = StringUtils.EMPTY;
            //TODO FIX the id
            Subsidiary subsidiary = subsidiaryRepository.findByRecordId(testResult.getRecordId());
            if (subsidiary != null) {
                subId = subsidiary.getIdentifier();
            }
            Map<String, Set<String>> errorMap = testResult.getErrorMap();
            if (errorMap != null && !errorMap.isEmpty()) {
                populateCategorySections(dashboard, errorMap, issuesMap);
                populateValidationErrorMsgs(dashboard, errorMap, issuesMap);
            }
            if (status != 1) {
                populateFailedData(dashboardTableDataList, testResult, passedCount, status, totalCount, subId, issuesMap);
            }
            totalPassedCount = totalPassedCount + passedCount;
            totalFailedCount = totalFailedCount + failedCount;
            skusMap.putAll(getTestStatus(passedCount, failedCount, totalCount));
            String statusKey = getStatus(status);
            if (testCasesMap.containsKey(statusKey)) {
                testCasesMap.put(statusKey, testCasesMap.get(statusKey) + 1);
            } else {
                testCasesMap.put(statusKey, 1);
            }
            Map<String, String> skuErrorMap = testResult.getFailedSkusError();
            if (skuErrorMap != null && !skuErrorMap.isEmpty()) {
                Map<String, String> skuErrorMapUpdated = new HashMap<>();
                skuErrorMap.keySet().forEach(sku -> {
                    skuErrorMapUpdated.put(sku + "|" + testResult.getTestName(), skuErrorMap.get(sku));
                });
                skuErrorMapList.add(skuErrorMapUpdated);
            }
            subsidiaryDataList.add(populateSubsidiaryData(testResult, subId, subsidiaryDataList));
        }
        qaSummaryData.setSkuErrorMapList(skuErrorMapList);
        qaSummaryData.setSubsidiaryData(subsidiaryDataList.stream().collect(Collectors.toList()));
        qaSummaryData.setIssuesMap(issuesMap);
        qaSummaryData.setSkusMap(skusMap);
        qaSummaryData.setFailedData(dashboardTableDataList.size() > 10 ? dashboardTableDataList.subList(0, 9) : dashboardTableDataList);
        qaSummaryData.setTotalSkuCount(totalPassedCount + totalFailedCount);
        qaSummaryData.setTotalTestCaseCount(qaTestResultList.size());
        if (!issuesMap.isEmpty()) {
            populateIssueChartData(qaSummaryData, issuesMap, issuesChartMap);
        }
        if (testCasesMap != null && !testCasesMap.isEmpty()) {
            for (String key : testCasesMap.keySet()) {
                double value = testCasesMap.get(key);
                double totalCount = qaTestResultList.size();
                Double percentage = (value / totalCount) * 100;
                testCasesMap.put(key, percentage.intValue());
            }
        }
        qaSummaryData.setTestCasesMap(testCasesMap);
    }

    private void populateValidationErrorMsgs(Dashboard dashboard, Map<String, Set<String>> errorMap, Map<String, Map<String, Set<String>>> issuesMap) {
        if (errorMap.containsKey(VALIDATION)) {
            Map<String, Set<String>> valuesMap = issuesMap.get(BUYING_CONFIGURATOR);
            if (valuesMap == null) {
                valuesMap = new HashMap<>();
            }
            Set<String> values = new TreeSet<>();
            values.addAll(errorMap.get(VALIDATION));
            valuesMap.put(VALIDATION, values);
            issuesMap.put(BUYING_CONFIGURATOR, valuesMap);
        }
    }

    private DashboardTableData populateSubsidiaryData(QATestResult testResult, String subId, Set<DashboardTableData> subsidiaryDataList) {
        DashboardTableData dashboardTableData = new DashboardTableData();

        dashboardTableData.setSubsidiary(subId);
        dashboardTableData.setSite(testResult.getSite());
        dashboardTableData.setPassedSkus(String.valueOf(testResult.getTestPassedCount()));
        dashboardTableData.setFailedSkus(String.valueOf(testResult.getTestFailedCount()));
        if (CollectionUtils.isNotEmpty(subsidiaryDataList)) {
            Optional<DashboardTableData> dashboardTableDataOptional = subsidiaryDataList.stream().filter(sub -> sub.getSubsidiary().equalsIgnoreCase(subId)).findFirst();
            if (dashboardTableDataOptional.isPresent()) {
                dashboardTableData = dashboardTableDataOptional.get();
                dashboardTableData.setPassedSkus(String.valueOf(testResult.getTestPassedCount() + Integer.parseInt(dashboardTableData.getPassedSkus())));
                dashboardTableData.setFailedSkus(String.valueOf(testResult.getTestFailedCount() + Integer.parseInt(dashboardTableData.getFailedSkus())));
            }
        }
        return dashboardTableData;
    }

    private int calculateImpact(Map<String, Map<String, Set<String>>> issuesMap) {
        int impact = 0;
        if (issuesMap != null && !issuesMap.isEmpty()) {
            List<ImpactConfig> impactConfigs = impactConfigRepository.findAll();
            if (CollectionUtils.isNotEmpty(impactConfigs)) {
                for (ImpactConfig impactConfig : impactConfigs) {
                    for (ImpactLabelDto labels : impactConfig.getLabels()) {
                        for (String key : issuesMap.keySet()) {
                            for (String mapKey : issuesMap.get(key).keySet()) {
                                if (labels.getLabels().contains(mapKey)) {
                                    impact = impact + (labels.getImpact() * labels.getMultiplier());
                                }
                            }
                        }
                    }
                }
            }
        }
        return impact;
    }

    private void populateFailedData(List<DashboardTableData> dashboardTableDataList, QATestResult testResult, int passedCount, int status, int totalCount, String subId, Map<String, Map<String, Set<String>>> issuesMap) {
        DashboardTableData failedData = new DashboardTableData();
        failedData.setStatus(status == 2 ? FAILED : PARTIALLY_PASSED);
        failedData.setSubsidiary(subId);
        failedData.setSite(testResult.getSite());
        failedData.setTestCase(String.valueOf(testResult.getLocatorGroupIdentifier()));
        Double percentage = (Double.valueOf(passedCount) / Double.valueOf(totalCount)) / 100;
        failedData.setPassedSkus(passedCount + " over " + totalCount + " (" + percentage.intValue() + "%)");
        failedData.setImpact(calculateImpact(issuesMap));
        dashboardTableDataList.add(failedData);
    }

    private void populateCategorySections(Dashboard dashboard, Map<String, Set<String>> errorMap, Map<String, Map<String, Set<String>>> issuesMap) {
        String recordId = dashboard.getDashboardProfile();
        if (recordId == null) {
            //OLD way rendering
            issuesMap.put("all category", errorMap);
        } else {
            // Profile based rendering
            DashboardProfile dashboardProfile = dashboardProfileService.getDashboardProfileByRecordId(dashboard.getDashboardProfile());
            if (dashboardProfile == null) {
                issuesMap.put("all category", errorMap);
            } else {
                List<DashboardLabelDto> labels = dashboardProfile.getLabels();
                if (CollectionUtils.isNotEmpty(labels)) {
                    errorMap.keySet().forEach(key -> {
                        labels.forEach(dashboardLabelDto -> {
                            Map<String, Set<String>> allChildren = new HashMap<>();
                            if (key.equals(dashboardLabelDto.getParent())) {
                                dashboardLabelDto.getChildren().forEach(child -> {
                                    Set<String> childErrorTree = new TreeSet<>();
                                    Set<String> childError = errorMap.get(child);
                                    if (CollectionUtils.isNotEmpty(childError)) {
                                        childErrorTree.addAll(childError.stream().filter(errorMsg -> StringUtils.isNotEmpty(errorMsg)).collect(Collectors.toSet()));
                                    }
                                    if (CollectionUtils.isNotEmpty(childErrorTree)) {
                                        allChildren.put(child, childErrorTree);
                                    }
                                });
                                if (!allChildren.isEmpty()) {
                                    if (issuesMap.containsKey(key)) {
                                        mergeIssueMap(issuesMap, key, allChildren);
                                    } else {
                                        issuesMap.put(key, allChildren);
                                    }
                                }
                            }
                        });
                    });
                }
            }
        }
    }

    private void mergeIssueMap(Map<String, Map<String, Set<String>>> issuesMap, String key, Map<String, Set<String>> allChildren) {
        Map<String, Set<String>> mergedMap = new HashMap<>();
        Map<String, Set<String>> existingMap = issuesMap.get(key);
        mergedMap.putAll(existingMap);

        allChildren.forEach((mapKey, value) -> {
            //Get the value for key in map.
            Set<String> list = mergedMap.get(mapKey);
            if (list == null) {
                mergedMap.put(mapKey, value);
            } else {
                //Merge two list together
                Set<String> mergedValue = new TreeSet<>(value);
                mergedValue.addAll(list);
                mergedMap.put(mapKey, mergedValue);
            }
        });
        issuesMap.put(key, mergedMap);
    }

    private void populateTestResults(String subsidiaryId, String days, String runner, List<QATestResult> qaTestResultList, Dashboard dashboard) {
        String dashBoardId = String.valueOf(dashboard.getId());
        subsidiaryId = StringUtils.isEmpty(subsidiaryId) ? dashboard.getSubsidiary() : subsidiaryId;
        if (days.equalsIgnoreCase("0")) {
            populateQaResultForLastRun(subsidiaryId, runner, qaTestResultList, dashBoardId);
        } else {
            populateQAResultsForNDays(subsidiaryId, days, runner, qaTestResultList, dashBoardId);
        }
    }

    private void populateQAResultsForNDays(String subsidiaryId, String days, String runner, List<QATestResult> qaTestResultList, String dashBoardId) {
        if (StringUtils.isNotEmpty(subsidiaryId)) {
            String[] subsidiaryIds = subsidiaryId.split(",");
            for (String subId : subsidiaryIds) {
                if (StringUtils.isNotEmpty(runner)) {
                    for (String user : runner.split(",")) {
                        qaTestResultList.addAll(qaRepository.findByCreationTimeBetweenAndSubsidiaryAndUserAndDashboard(LocalDateTime.now().minusDays(Integer.parseInt(days)), LocalDateTime.now(), subId, user, dashBoardId));
                    }
                } else {
                    qaTestResultList.addAll(qaRepository.findByCreationTimeBetweenAndSubsidiaryEqualsAndDashboard(LocalDateTime.now().minusDays(Integer.parseInt(days)), LocalDateTime.now(), subId, dashBoardId));
                }
            }
        } else {
            if (StringUtils.isNotEmpty(runner)) {
                for (String user : runner.split(",")) {
                    qaTestResultList.addAll(qaRepository.findByCreationTimeBetweenAndUserAndDashboard(LocalDateTime.now().minusDays(Integer.parseInt(days)), LocalDateTime.now(), user, dashBoardId));
                }
            } else {
                qaTestResultList.addAll(qaRepository.findByCreationTimeBetweenAndDashboard(LocalDateTime.now().minusDays(Integer.parseInt(days)), LocalDateTime.now(), dashBoardId));
            }
        }
    }

    private void populateQaResultForLastRun(String subsidiaryId, String runner, List<QATestResult> qaTestResultList, String dashBoardId) {
        if (StringUtils.isNotEmpty(subsidiaryId)) {
            String[] subsidiaryIds = subsidiaryId.split(",");
            for (String subId : subsidiaryIds) {
                if (StringUtils.isNotEmpty(runner)) {
                    for (String user : runner.split(",")) {
                        qaTestResultList.addAll(qaRepository.findBySubsidiaryAndUserAndDashboardOrderByIdDesc(subId, user, dashBoardId));
                    }
                } else {
                    qaTestResultList.addAll(qaRepository.findBySubsidiaryAndDashboardOrderByIdDesc(subId, dashBoardId));
                }
            }
            if (CollectionUtils.isNotEmpty(qaTestResultList)) {
                QATestResult qaTestResult = qaTestResultList.get(0);
                String sessionId = qaTestResult.getSessionId();
                qaTestResultList.clear();
                for (String subId : subsidiaryIds) {
                    if (StringUtils.isNotEmpty(runner)) {
                        for (String user : runner.split(",")) {
                            qaTestResultList.addAll(qaRepository.findBySessionIdAndDashboardAndSubsidiaryAndUserOrderByIdDesc(sessionId, dashBoardId, subId, user));
                        }
                    } else {
                        qaTestResultList.addAll(qaRepository.findBySessionIdAndDashboardAndSubsidiaryOrderByIdDesc(sessionId, dashBoardId, subId));
                    }
                }
            }
        } else {
            if (StringUtils.isNotEmpty(runner)) {
                for (String user : runner.split(",")) {
                    qaTestResultList.addAll(qaRepository.findByUserAndDashboardOrderByIdDesc(user, dashBoardId));
                }
                if (CollectionUtils.isNotEmpty(qaTestResultList)) {
                    QATestResult qaTestResult = qaTestResultList.get(0);
                    String sessionId = qaTestResult.getSessionId();
                    qaTestResultList.clear();
                    for (String user : runner.split(",")) {
                        qaTestResultList.addAll(qaRepository.findBySessionIdAndDashboardAndUserOrderByIdDesc(sessionId, dashBoardId, user));
                    }
                }
            } else {
                qaTestResultList.addAll(qaRepository.findAllByDashboardOrderByIdDesc(dashBoardId));
                if (CollectionUtils.isNotEmpty(qaTestResultList)) {
                    QATestResult qaTestResult = qaTestResultList.get(0);
                    String sessionId = qaTestResult.getSessionId();
                    qaTestResultList.clear();
                    qaTestResultList.addAll(qaRepository.findBySessionIdAndDashboardOrderByIdDesc(sessionId, dashBoardId));
                }
            }
        }
    }

    Map<String, Integer> getTestStatus(int passedCount, int failedCount, int totalCount) {
        Double passedCountDb = Double.valueOf(passedCount);
        Double failedCountDb = Double.valueOf(failedCount);
        Double totalCountDb = Double.valueOf(totalCount);
        Double passPercentage = (passedCountDb / totalCountDb) * 100;
        Double failPercentage = (failedCountDb / totalCountDb) * 100;

        Map<String, Integer> statusPercentageMap = new HashMap<>();

        statusPercentageMap.put(FAILED, failPercentage.intValue());
        statusPercentageMap.put(SUCCESS, passPercentage.intValue());
        return statusPercentageMap;
    }

    String getStatus(int status) {
        switch (status) {
            case 1:
                return SUCCESS;
            case 2:
                return FAILED;
            case 3:
                return PARTIALLY_PASSED;
        }
        return FAILED;
    }

    private void populateLast7DaysIssuesData(QaSummaryData qaSummaryData, Dashboard dashboard, String subsidiaryId) {
        subsidiaryId = StringUtils.isEmpty(subsidiaryId) ? dashboard.getSubsidiary() : subsidiaryId;
        Map<String, Map<String, Integer>> issueChartDateMap = new HashMap<>();
        List<QATestResult> qaTestResultList = new ArrayList<>();

        populateTestResults(subsidiaryId, "7", null, qaTestResultList, dashboard);
        if (CollectionUtils.isNotEmpty(qaTestResultList)) {
            Set<String> users = new HashSet<>();
            for (QATestResult qaTestResult : qaTestResultList) {
                users.add(qaTestResult.getUser());
                Map<String, Integer> issueChartMap = new HashMap<>();
                Map<String, Map<String, Set<String>>> issuesMap = new HashMap<>();
                Map<String, Set<String>> errorMap = qaTestResult.getErrorMap();
                if (errorMap != null && !errorMap.isEmpty()) {
                    populateCategorySections(dashboard, errorMap, issuesMap);
                    populateValidationErrorMsgs(dashboard, errorMap, issuesMap);
                }
                if (issuesMap != null && !issuesMap.isEmpty()) {
                    issuesMap.values().forEach(issueMapValues -> {
                        issueMapValues.keySet().forEach(issueKey -> {
                            issueChartMap.put(issueKey, issueMapValues.get(issueKey).size());
                        });
                    });
                    Date date = qaTestResult.getCreationTime();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    String dayWeekText = new SimpleDateFormat("EEEE").format(date);
                    if (issueChartDateMap.containsKey(dayWeekText)) {
                        issueChartMap.putAll(issueChartDateMap.get(dayWeekText));
                    }
                    issueChartDateMap.put(dayWeekText, issueChartMap);
                }
                qaSummaryData.setUsers(users);
            }
        }
        qaSummaryData.setIssuesSummaryChartMap(issueChartDateMap);
    }
}
