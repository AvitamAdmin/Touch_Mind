package com.touchmind.qa.service;

import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.service.impl.CronService;
import com.touchmind.qa.framework.ThreadTestContext;
import org.json.JSONObject;
import org.testng.ITestContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public interface QualityAssuranceService extends CronService {

    void runTest(String parameterMap);

    void saveTestResult(QATestResult qaTestResult, ITestContext context, JSONObject testData, AtomicBoolean wasTestPassed, Date startTime, Date endTime, String sessionId, String currentUser, Object sku, String locatorGroupIdentifier);

    void initializeData(JSONObject testMapData, ThreadTestContext threadTestContext, ITestContext context);

    void saveErrorData(LocatorPriority locatorGroupData, JSONObject testData, TestLocator testLocator, String details);
}
