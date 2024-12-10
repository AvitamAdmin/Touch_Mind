package com.cheil.qa.service;

import com.cheil.core.mongo.model.LocatorPriority;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.service.impl.CronService;
import com.cheil.qa.framework.ThreadTestContext;
import org.json.JSONObject;
import org.testng.ITestContext;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public interface QualityAssuranceService extends CronService {

    void runTest(String parameterMap);

    void saveTestResult(ITestContext context, JSONObject testData, Object locatorGroupIdentifier, AtomicBoolean wasTestPassed, Date startTime, Date endTime, String sessionId, String currentUser, Object sku);

    void initializeData(JSONObject testMapData, ThreadTestContext threadTestContext, ITestContext context);

    void saveErrorData(LocatorPriority locatorGroupData, JSONObject testData, TestLocator testLocator, String details);
}
