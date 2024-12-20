package com.touchmind.qa.testplans;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.touchmind.core.SpringContext;
import com.touchmind.core.mongo.model.TestLocatorGroup;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.pages.concretepages.testPlans.AbstractTestPlan;
import static com.touchmind.qa.service.impl.QualityAssuranceServiceImpl.REPORT_FILE_NAME;
import com.touchmind.qa.utils.QaConstants;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


@AllArgsConstructor
public class CoreTest extends AbstractTestPlan {

    public static final String MISSING_LOCATOR_GROUPS = "MissingLocatorGroups";
    public static final String WAS_NO_LOCATOR_GROUP_FOUND_TO_EXECUTE_THE_TEST_PLAN = "There was no test journey configured to execute the test plan, ignoring the test plan";


    @Test(dataProvider = "inputSkus")
    public void processLocators(ITestContext context, Object[] skus) throws MalformedURLException {
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        Object cronSessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
        String sessionId = ObjectUtils.isNotEmpty(cronSessionId) ? cronSessionId.toString() : (String) context.getSuite().getAttribute(TestDataUtils.Field.SESSION_ID.toString());
        LocatorGroupService locatorGroupService = SpringContext.getBean(LocatorGroupService.class);
        List<LocatorGroupData> testPlans = getLocatorsByTestPlan(getTestPlanId(context));
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        if (CollectionUtils.isEmpty(testPlans)) {
            String reportFileName = getReportFileName(MISSING_LOCATOR_GROUPS, sessionId);
            testData.put(REPORT_FILE_NAME, reportFileName);
            getExtentManager(context).startNewReport(reportFileName);
            ExtentTest extentTest = getExtentManager(context).startNewTest(MISSING_LOCATOR_GROUPS, "Nothing to test !!");
            threadTestContext.setExtentTest(extentTest);
            ReportUtils.fail(context, WAS_NO_LOCATOR_GROUP_FOUND_TO_EXECUTE_THE_TEST_PLAN, StringUtils.EMPTY, false);
            getQualityAssuranceService().saveTestResult(context, testData, MISSING_LOCATOR_GROUPS, new AtomicBoolean(false), context.getStartDate(), Calendar.getInstance().getTime(), sessionId, String.valueOf(testData.get("currentUser")), StringUtils.EMPTY);
            return;
        }
        testPlans.stream().forEach(locatorGroupData -> {
            TestLocatorGroup testLocatorGroup = locatorGroupService.findLocatorByGroupId(locatorGroupData.getGroupId());
            String reportFileName = getReportFileName(testLocatorGroup.getIdentifier(), sessionId);
            testData.put(REPORT_FILE_NAME, reportFileName);
            getExtentManager(context).startNewReport(reportFileName);
            AtomicBoolean wasTestPassed = new AtomicBoolean(true);
            Arrays.stream(skus).forEach(sku -> {
                getQualityAssuranceService().initializeData(testData, threadTestContext, context);
                ExtentTest extentTest = getExtentManager(context).startNewTest(sku.toString(), testLocatorGroup.getIdentifier());
                threadTestContext.setExtentTest(extentTest);
                ReportUtils.logMessage(context, isDebug, "=== New report: " + reportFileName);
                ReportUtils.logMessage(context, isDebug, "=== Test case: " + testLocatorGroup.getIdentifier());
                ReportUtils.logMessage(context, isDebug, "=== New Test: " + testLocatorGroup.getIdentifier() + " > " + sku);
                try {
                    wasTestPassed.set(getActionFactory().performAction(context, locatorGroupData, sku).getStepStatus().equals(Status.PASS));
                } catch (Exception exp) {
                    wasTestPassed.set(false);
                    ReportUtils.logMessage(context, isDebug, "=== Test failed: " + exp.getMessage());
                    Reporter.getCurrentTestResult().setStatus(ITestResult.FAILURE);
                }
                ReportUtils.logMessage(context, isDebug, "=== closing driver: ");
                WebDriver driver = threadTestContext.getDriver();
                driver.close();
                driver.quit();
                ReportUtils.logMessage(context, isDebug, "=== driver closed test status : " + (wasTestPassed.get() ? ITestResult.SUCCESS : ITestResult.FAILURE));
                Reporter.getCurrentTestResult().setStatus(wasTestPassed.get() ? ITestResult.SUCCESS : ITestResult.FAILURE);
                ReportUtils.logMessage(context, isDebug, "=== save result started: ");
                getQualityAssuranceService().saveTestResult(context, testData, testLocatorGroup.getIdentifier(), wasTestPassed, context.getStartDate(), Calendar.getInstance().getTime(), sessionId, String.valueOf(testData.get("currentUser")), sku);
                ReportUtils.logMessage(context, isDebug, "=== save result end: ");
            });
        });
        // getQualityAssuranceService().saveTestSummary(sessionId);
        String processType = TestDataUtils.getString(testData, TestDataUtils.Field.JOB_TYPE);
        if (StringUtils.isNotEmpty(processType) && "cronJob".equals(processType)) {
            ReportUtils.logMessage(context, isDebug, "=== Notification : " + testData);
//            getMessageResourceService().processNotifications(testData);
            ReportUtils.logMessage(context, isDebug, "=== Notification processed : " + testData);
        }
    }

    private String getReportFileName(Object locatorGroupIdentifier, String sessionId) {
        String currentTime = new SimpleDateFormat(QaConstants.DATE_FORMAT).format(Calendar.getInstance().getTime());
        return locatorGroupIdentifier + "_" + currentTime + "_" + sessionId;
    }

    @DataProvider(name = "inputSkus")
    public Object[][] myDataProvider(ITestContext context) {
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String value = testData.getString(TestDataUtils.Field.SKUS.toString());
        String[] skus = value.split(",");
        Object[][] data = new Object[1][skus.length];
        System.arraycopy(skus, 0, data[0], 0, skus.length);
        return data;
    }
}
