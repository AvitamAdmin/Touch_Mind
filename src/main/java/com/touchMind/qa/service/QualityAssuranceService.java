package com.touchMind.qa.service;

import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongotemplate.QATestResult;
import com.touchMind.core.service.impl.CronService;
import com.touchMind.form.ErrorTypeForm;
import com.touchMind.form.QATestResultForm;
import com.touchMind.form.QaResultErrorMapperForm;
import com.touchMind.qa.framework.ThreadTestContext;
import org.json.JSONObject;
import org.testng.ITestContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public interface QualityAssuranceService extends CronService {

    void runTest(String parameterMap);

    void saveTestResult(QATestResult qaTestResult, ITestContext context, JSONObject testData, AtomicBoolean wasTestPassed, Date startTime, Date endTime, String sessionId, String currentUser, Object sku, String locatorGroupIdentifier);

    void initializeData(JSONObject testMapData, ThreadTestContext threadTestContext, ITestContext context);

    void saveErrorData(LocatorPriority locatorGroupData, JSONObject testData, TestLocator testLocator, String details);

    void saveErrorType(QATestResultForm qaTestResultForm, QATestResult qaTestResult);

    void saveErrorTypeModel(ErrorTypeForm errorTypeForm);

    void saveErrorMapperModel(QaResultErrorMapperForm qaResultErrorMapperForm);

    void populateTestData(JSONObject testMapData);
}
