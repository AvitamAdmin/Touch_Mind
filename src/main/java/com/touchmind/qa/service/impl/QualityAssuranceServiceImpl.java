package com.touchmind.qa.service.impl;

import com.touchmind.core.SpringContext;
import com.touchmind.core.mongo.model.*;
import com.touchmind.core.mongo.repository.CronJobProfileRepository;
import com.touchmind.core.mongo.repository.QaTestPlanRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.TestPlanService;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.mail.service.MailService;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.qa.utils.TestDataUtils.Field;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;
import org.testng.TestNG;

import java.lang.System;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class QualityAssuranceServiceImpl implements QualityAssuranceService, BaseService {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String REPORT_PATH = "/reports/";
    public static final String ENVIRONMENT = "environment";
    public static final String SHORTCUTS = "shortcuts";
    public static final String REPORT_FILE_NAME = "reportFileName";
    public static final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
    public static final String VALIDATION = "Validation";
    private static final Logger LOG = LoggerFactory.getLogger(QualityAssuranceServiceImpl.class);
    private static final String DELIM = " \n\r\t,.;";
    @Autowired
    private MailService mailService;
    @Autowired
    private Gson gson;
//    @Autowired
//    private VariantRepository variantRepository;
//    @Autowired
//    private ModelService modelService;
  @Autowired
    private Environment env;
    @Autowired
    private QARepository qaRepository;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;
    @Autowired
    private TestPlanService testPlanService;

    private static void populateErrorData(QATestResult qaTestResult, Map<String, Set<String>> errorMsg) {
        if (!errorMsg.isEmpty()) {
            Map<String, Set<String>> errorMap = qaTestResult.getErrorMap();
            if (errorMap != null && !errorMap.isEmpty()) {
                Map<String, Set<String>> mergedMap = new HashMap<>();
                mergedMap.putAll(errorMap);

                errorMsg.forEach((key, value) -> {
                    //Get the value for key in map.
                    Set<String> list = mergedMap.get(key);
                    if (list == null) {
                        mergedMap.put(key, value);
                    } else {
                        //Merge two list together
                        Set<String> mergedValue = new HashSet<>(value);
                        mergedValue.addAll(list);
                        mergedMap.put(key, mergedValue);
                    }
                });
                LOG.info("mergedMap" + mergedMap);
                qaTestResult.setErrorMap(mergedMap);
            } else {
                qaTestResult.setErrorMap(errorMsg);
            }
        }
    }

    private static String populateOrderNumbers(JSONObject testData, QATestResult qaTestResult) {
        String orderNumber = StringUtils.EMPTY;
        for (String key : testData.keySet()) {
            if (StringUtils.containsIgnoreCase(key, Field.ORDER_NUMBER.toString())) {
                orderNumber = testData.get(key).toString();
                if (StringUtils.isNotEmpty(qaTestResult.getOrderNumber())) {
                    orderNumber = qaTestResult.getOrderNumber() + "," + orderNumber;
                }
                break;
            }
        }
        return orderNumber;
    }

    @Override
    public void runTest(String testMapData) {
        try {
            TestNG testNG = SpringContext.getBean(TestNG.class, testMapData);
            testNG.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveTestResult(ITestContext context, final JSONObject testData, Object locatorGroupIdentifier, AtomicBoolean wasTestPassed, Date startTime, Date endTime, String sessionId, String currentUser, Object sku) {
        LOG.debug("Inside saveTestResult" + testData);
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        String testPlanId = testData.getString(TestDataUtils.Field.TEST_PLAN.toString());
        TestPlan testPlan = testPlanService.getTestPlanByRecordId(testPlanId);
        ReportUtils.logMessage(context, isDebug, "=== Test Resul save: " + testData);
        String fileName = String.valueOf(testData.get(REPORT_FILE_NAME));
        int totalTestCount = testData.optInt(Field.TEST_COUNT.toString(), 0);
        String subsidiary = testData.getString(Field.SUBSIDIARY.toString());
        List<QATestResult> testResultList = qaRepository.findBySessionIdAndLocatorGroupIdentifierAndTestName(sessionId, locatorGroupIdentifier, testPlan.getIdentifier());
        //TODO move to ActionFactor
        QATestResult qaTestResult = CollectionUtils.isNotEmpty(testResultList) ? testResultList.stream().findFirst().get() : new QATestResult();
        qaTestResult.setTestName(testData.getString(Field.TEST_NAME.toString()));
        qaTestResult.setTestNameDescription(testPlan.getShortDescription());
        qaTestResult.setSku(populateSkuData(sku, qaTestResult));
        qaTestResult.setSessionId(sessionId);
        qaTestResult.setReportFilePath(env.getProperty("server.url") + REPORT_PATH + fileName + ".html");
        qaTestResult.setLocatorGroupIdentifier(locatorGroupIdentifier);
        Object errorMsg = TestDataUtils.getMap(testData, Field.ERROR_DATA);
        String dashboard = TestDataUtils.getString(testData, Field.DASHBOARD);
        if (StringUtils.isNotEmpty(dashboard)) {
            qaTestResult.setDashboard(dashboard);
        }
        if (errorMsg != null) {
            populateErrorData(qaTestResult, (Map<String, Set<String>>) errorMsg);
        }
        int passedCount = qaTestResult.getTestPassedCount();
        int failedCount = qaTestResult.getTestFailedCount();
        if (wasTestPassed.get()) {
            passedCount++;
        } else {
            String errorMessage = TestDataUtils.getString(testData, Field.ERROR_MSG);
            if (StringUtils.isNotEmpty(errorMessage)) {
                qaTestResult.setFailedSkusError(populateFailedSkuData(sku, qaTestResult, errorMessage));
            }
            failedCount++;
        }
        if (passedCount == totalTestCount) {
            qaTestResult.setResultStatus(1);
        } else if (passedCount == 0) {

            qaTestResult.setResultStatus(2);
        } else {
            qaTestResult.setResultStatus(3);
        }
        qaTestResult.setCreationTime(Calendar.getInstance().getTime());
        qaTestResult.setTestPassedCount(passedCount);
        qaTestResult.setTestFailedCount(failedCount);
        qaTestResult.setOrderNumber(populateOrderNumbers(testData, qaTestResult));
        qaTestResult.setStartTime(startTime);
        qaTestResult.setEndTime(endTime);
        qaTestResult.setTimeTaken(endTime.getTime() - startTime.getTime());
        qaTestResult.setUser(currentUser);
        qaTestResult.setSubsidiary(subsidiary);
        qaTestResult.setPaymentType(TestDataUtils.getString(testData, Field.PAYMENT_TYPE));
        qaTestResult.setDeliveryType(TestDataUtils.getString(testData, Field.DELIVERY_TYPE));
        qaTestResult.setSite(TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
        qaRepository.save(qaTestResult);
       //TODO remove the recordId from QaTestResultTable and also from QaLocatorResultReport which is buggy
        qaTestResult.setRecordId(String.valueOf(qaTestResult.getId().getTimestamp()));
        qaRepository.save(qaTestResult);
        ReportUtils.logMessage(context, isDebug, "=== Saved Test Resul : " + qaTestResult);
        LOG.info("End saveTestResult");
    }

    private String populateSkuData(Object sku, QATestResult qaTestResult) {
        String skuStr = StringUtils.EMPTY;
        if (ObjectUtils.isNotEmpty(sku)) {
            skuStr = sku.toString();
            if (StringUtils.isNotEmpty(qaTestResult.getSku())) {
                skuStr = skuStr + "," + qaTestResult.getSku();
            }
        }
        return skuStr;
    }

    private Map<String, String> populateFailedSkuData(Object sku, QATestResult qaTestResult, String errorMsg) {
        Map<String, String> skuErrorMap = new HashMap<>();
        try {
            if (StringUtils.isNotEmpty(errorMsg)) {
                Map<String, String> skuMap = qaTestResult.getFailedSkusError();
                if (skuMap != null) {
                    skuErrorMap.putAll(skuMap);
                }
                skuErrorMap.put(sku.toString(), errorMsg);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return skuErrorMap;
    }

    public String getVariantAsCommaSeparatedList(String skus) {
        StringBuffer buffer = new StringBuffer();
        if (StringUtils.isNotEmpty(skus)) {
            StringTokenizer st = new StringTokenizer(skus, DELIM);
            String prefix = "";
            while (st.hasMoreTokens()) {
                buffer.append(prefix);
                prefix = TestDataUtils.COMMA;
                buffer.append(st.nextToken());
            }
        }
        return buffer.toString();
    }

    private void populateSkuData(Map<String, String> testMapData) {
        String skus = testMapData.get(Field.SKUS.toString());
        if (StringUtils.isNotEmpty(skus)) {
            testMapData.put(Field.SKUS.toString(), getVariantAsCommaSeparatedList(skus));
        } else {
            String models = testMapData.get(SHORTCUTS);
            if (StringUtils.isNotEmpty(models)) {
               // List<String> skuList = modelService.getVariantsListForModels(Arrays.asList(models.split(TestDataUtils.COMMA)));
              //  testMapData.put(Field.SKUS.toString(), StringUtils.join(skuList, TestDataUtils.COMMA));
            }
        }
        testMapData.put(Field.TEST_COUNT.toString(), String.valueOf(testMapData.get(Field.SKUS.toString()).split(TestDataUtils.COMMA).length));
    }

//    private void runTestPlan(Map<String, String> data) {
//        String testPlans = data.get("cronTestPlanFormList");
//        if (StringUtils.isNotEmpty(testPlans)) {
//            Type listType = new TypeToken<ArrayList<CronTestPlanDto>>() {
//            }.getType();
//            List<CronTestPlanDto> cronTestPlans = gson.fromJson(testPlans, listType);
//            if (CollectionUtils.isNotEmpty(cronTestPlans)) {
//                cronTestPlans.forEach(cronTestPlanForm -> {
//                    StringBuffer skus = new StringBuffer();
//                    if (StringUtils.isNotEmpty(cronTestPlanForm.getCategoryId())) {
//                        List<Variant> variants = variantRepository.findByCategory_identifier(cronTestPlanForm.getIdentifier());
//                        variants.forEach(variant -> {
//                            if (StringUtils.isNotEmpty(cronTestPlanForm.getSubsidiary()) && CollectionUtils.isNotEmpty(variant.getSubsidiaries())) {
//                                //TODO check if the record is correctly fetched
//                                Subsidiary subsidiary = subsidiaryRepository.findByRecordId(cronTestPlanForm.getSubsidiary());
//                                if (subsidiary != null && variant.getSubsidiaries().contains(subsidiary.getIdentifier())) {
//                                    skus.append(variant.getIdentifier() + ",");
//                                }
//                            } else {
//                                LOG.debug("No subsidiary configured in cronjob or subsidiaries missing for variant - " + variant.getIdentifier());
//                            }
//                        });
//                    }
//                    if (StringUtils.isNotEmpty(skus.toString())) {
//                        String skusData = skus.toString();
//                        data.put("skus", skusData.substring(0, skusData.length() - 1));
//                        LOG.debug("==SKU " + skusData + " TO BE PROCESSED FOR category " + cronTestPlanForm.getCategoryId());
//                    }
//                    data.put("testProfile", cronTestPlanForm.getTestProfile());
//                    data.put("environment", cronTestPlanForm.getEnvironment());
//                    data.put("subsidiary", cronTestPlanForm.getSubsidiary());
//                    data.put("testPlan", cronTestPlanForm.getTestPlan());
//                    data.put("siteIsoCode", cronTestPlanForm.getSiteIsoCode());
//                    processDataInternal(data);
//                    mailService.sendMailForSub(data, populateEmailForProfile(cronTestPlanForm.getCronProfileId()), cronTestPlanForm.getTestPlan());
//                });
//            }
//        }
//    }

//    @Override
//    public List<BaseEntity> getReport(ReportDto reportDto) {
//        return null;
//    }

    @Override
    public Map<String, List<String>> getHeaders(Node node, String subsidiary, String mapping) {
        return null;
    }

//    @Override
//    public void processData(Map<String, String> data) {
//        runTestPlan(data);
//        String cronProfileId = data.get(Field.CRON_PROFILE_ID.toString());
//        if (StringUtils.isNotEmpty(cronProfileId)) {
//            data.put(Field.EMAILS.toString(), populateEmailForProfile(cronProfileId));
//        }
//        mailService.sendMail(data);
//    }

    private String populateEmailForProfile(String cronProfileId) {
        if (StringUtils.isNotEmpty(cronProfileId)) {
            Optional<CronJobProfile> cronJobProfileOptional = cronJobProfileRepository.findById(new ObjectId(cronProfileId));
            if (cronJobProfileOptional.isPresent()) {
                return cronJobProfileOptional.get().getRecipients();
            }
        } else {
            LOG.error("No Cron Profile configured for sending mail");
        }
        return null;
    }

//    public void processDataInternal(Map<String, String> data) {
//        data.put("currentUser", data.get(Field.CRON_CURRENT_USER.toString()));
//        String subsidiaryId = data.get(Field.SUBSIDIARY.toString());
//        //TODO check if the record is correctly fetched
//        Subsidiary subsidiary = subsidiaryRepository.findByRecordId(subsidiaryId);
//        if (subsidiary != null) {
//            subsidiaryId = subsidiary.getIdentifier();
//        }
//        saveCronHistory(data.get(TestDataUtils.Field.SESSION_ID.toString()), subsidiaryId, data.get(Field.EMAILS.toString()), 0, data.get(Field.JOB_TIME.toString()), "Running", "", data.get(Field.TEST_NAME.toString()));
//        try {
//            runTest(objectMapper.writeValueAsString(data));
//        } catch (JsonProcessingException e) {
//            LOG.error(e.getMessage());
//        }
//        int count = data.get("skus").split(",").length;
//        saveCronHistory(data.get(TestDataUtils.Field.SESSION_ID.toString()), subsidiaryId, data.get(Field.EMAILS.toString()), count, data.get(Field.JOB_TIME.toString()), "Completed", "", data.get(Field.TEST_NAME.toString()));
//    }

    @Override
    public void initializeData(JSONObject testMapData, ThreadTestContext threadContext, ITestContext context) {
        Map<String, Set<String>> errorMap = new HashMap<>();
        testMapData.put(Field.ERROR_DATA.toString(), errorMap);
        testMapData.put(Field.ERROR_MSG.toString(), StringUtils.EMPTY);
        try {
            threadContext.setDriver(SpringContext.getBean(WebDriver.class));
            threadContext.setFluentWait(SpringContext.getBean(FluentWait.class, threadContext.getDriver()));
            context.setAttribute(Field.THREAD_CONTEXT.toString(), threadContext);
        } catch (Exception e) {
            LOG.error(e.getMessage() + e + System.lineSeparator() + Arrays.toString(Thread.currentThread().getStackTrace()));
        }
    }

//    @Override
//    public void saveErrorData(LocatorPriority locatorGroupData, JSONObject testData, TestLocator testLocator, String details) {
//
//    }

    @Override
    public void saveErrorData(LocatorPriority locatorPriority, JSONObject testData, TestLocator testLocator, String details) {
        Map<String, Set<String>> errorMap = new HashMap<>();
        Object errorMapObj = TestDataUtils.getMap(testData, Field.ERROR_DATA);
        if (null != errorMapObj) {
            errorMap = (Map<String, Set<String>>) errorMapObj;
        }
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        if (locatorPriority != null && testLocator != null) {
            LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
            String errorMessage = locatorSelectorDto !=null ? locatorSelectorDto.getErrorMsg():"";
            if ((StringUtils.isNotEmpty(locatorPriority.getErrorMsg()) || StringUtils.isNotEmpty(errorMessage)) && CollectionUtils.isNotEmpty(testLocator.getLabels())) {
                String value = StringUtils.isNotEmpty(locatorPriority.getErrorMsg()) ? locatorPriority.getErrorMsg() : errorMessage;
                testData.put(Field.ERROR_MSG.toString(), value);
                if (StringUtils.isNotEmpty(value)) {
                    for (String label : testLocator.getLabels()) {
                        Set<String> values = new HashSet<>();
                        if (errorMap.containsKey(label)) {
                            values.addAll(errorMap.get(label));
                        }
                        values.add(value);
                        errorMap.put(label, values);
                    }
                    testData.put(Field.ERROR_DATA.toString(), errorMap);
                }
            }
        } else {
            Set<String> values = new HashSet<>();
            if (errorMap.containsKey(VALIDATION)) {
                values.addAll(errorMap.get(VALIDATION));
            }
            values.add(details);
            errorMap.put(VALIDATION, values);
            testData.put(Field.ERROR_DATA.toString(), errorMap);
        }
    }

    @Override
    public void processData(Map<String, String> data) {

    }

    @Override
    public void populateCommonData(CommonFields requestData) {

    }

    @Override
    public CommonFields validateIdentifier(String entityName, String identifier) {
        return null;
    }

//    @Override
//    public String saveSearchQuery(SavedQueryDto savedQueryDto, String source) {
//        return null;
//    }
//
//    @Override
//    public List<SavedQueryDto> getSavedQuery(String source) {
//        return null;
//    }
}

