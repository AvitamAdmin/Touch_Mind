package com.touchmind.qa.service;


import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.service.impl.CronService;
import com.touchmind.qa.framework.ThreadTestContext;
import org.json.JSONObject;
import org.testng.ITestContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public interface QualityAssuranceService extends CronService {

    void runTest(String parameterMap);

    void saveTestResult(ITestContext context, JSONObject testData, Object locatorGroupIdentifier, AtomicBoolean wasTestPassed, Date startTime, Date endTime, String sessionId, String currentUser, Object sku);

   // Map<String, List<String>> getHeaders(Node node, String subsidiary, String mapping);

    Map<String, List<String>> getHeaders(Node node, String subsidiary, String mapping);

    void initializeData(JSONObject testMapData, ThreadTestContext threadTestContext, ITestContext context);

    void saveErrorData(LocatorPriority locatorPriority, JSONObject testData, TestLocator testLocator, String details);

    //void saveErrorData(LocatorPriority locatorGroupData, JSONObject testData, TestLocator testLocator, String details);
}
