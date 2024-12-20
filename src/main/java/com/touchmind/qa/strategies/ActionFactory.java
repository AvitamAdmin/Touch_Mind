package com.touchmind.qa.strategies;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.pages.concretepages.testPlans.AbstractTestPlan;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.utils.ReportUtils;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import static com.touchmind.qa.utils.ReportUtils.savePageSource;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.qa.utils.WaitUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;

import java.util.List;
import java.util.Map;

@Component
public class ActionFactory extends AbstractTestPlan {

    private final Map<String, ElementActionService> actionServiceMap;
    Logger LOG = LoggerFactory.getLogger(ActionFactory.class);
    @Value("${testng.webdriver.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    @Autowired
    Environment env;
    @Autowired
    private SelectorService selectorService;
//    @Autowired
//    private CampaignRepository campaignRepository;

    public ActionFactory(Map<String, ElementActionService> actionServiceMap) {
        this.actionServiceMap = actionServiceMap;
    }

    public static boolean isElementPresentOnPage(WebElement webElement) {
        return webElement != null;
    }

    public ElementActionService getActionService(String actionType) {
        ElementActionService actionService = actionServiceMap.get(actionType);
        if (actionService == null) {
            throw new RuntimeException("Unsupported action type");
        }
        return actionService;
    }
/*
    public void performGlobalAction(ITestContext context, LocatorGroupData locatorGroupData, Object sku, List<QaLocatorResultReport> qaLocatorResultReports, QaResultReport qaResultReport) throws Exception {

        ActionResult result = performInternalGlobalAction(ActionType.VALIDATE_EPP_SSO_LINK, context, locatorGroupData, sku);
        if (result.getStepStatus().equals(Status.FAIL)) {
            ReportUtils.fail(context, ActionType.VALIDATE_EPP_SSO_LINK + " : " + " Failed to validate!", StringUtils.EMPTY, locatorGroupData.isTakeAScreenshot());
            qaLocatorResultReports.addAll(result.getQaLocatorResultReports());
            qaResultReport.setStatus(false);
            //TODO
            //qaResultReport.setQaLocatorResultReports(qaLocatorResultReports);
            qaResultReportRepository.save(qaResultReport);
            throw new Exception("Validation failed ");
        }
        result = performInternalGlobalAction(ActionType.TOOL_KIT_API_VALIDATION_ACTION, context, locatorGroupData, sku);
        if (result.getStepStatus().equals(Status.FAIL)) {
            ReportUtils.fail(context, ActionType.TOOL_KIT_API_VALIDATION_ACTION + " : " + " Failed to validate!", StringUtils.EMPTY, locatorGroupData.isTakeAScreenshot());
            qaLocatorResultReports.addAll(result.getQaLocatorResultReports());
            qaResultReport.setStatus(false);
            //TODO
            //qaResultReport.setQaLocatorResultReports(qaLocatorResultReports);
            qaResultReportRepository.save(qaResultReport);
            throw new Exception("Validation failed ");
        }


        result = performInternalGlobalAction(ActionType.ENVIRONMENT_ACTION, context, locatorGroupData, sku);
        qaLocatorResultReports.addAll(result.getQaLocatorResultReports());
        if (result.getStepStatus().equals(Status.FAIL)) {
            ReportUtils.fail(context, ActionType.ENVIRONMENT_ACTION + " : " + " Failed to open!", StringUtils.EMPTY, locatorGroupData.isTakeAScreenshot());
            qaLocatorResultReports.addAll(result.getQaLocatorResultReports());
            qaResultReport.setStatus(false);
            //TODO
            //qaResultReport.setQaLocatorResultReports(qaLocatorResultReports);
            qaResultReportRepository.save(qaResultReport);
            throw new Exception("Validation failed ");
        }
    }
*/
    public ActionResult performPreAction(ITestContext context, LocatorGroupData locatorGroupData, TestLocator locator, LocatorPriority locatorPriority) {
        //performInternalPreAction(ActionType.TOOL_KIT_API_VALIDATION_ACTION, context, locatorGroupData, locator, locatorPriority);
        ActionResult actionResult = performInternalPreAction(ActionType.SET_DATA_TYPE_ACTION, context, locatorGroupData, locator, locatorPriority);
        // CHeck if wait is enabled if yes wait for element to load
        if (BooleanUtils.isTrue(locatorPriority.getWaitForElementVisibleAndClickable())) {
            performInternalPreAction(ActionType.WAIT_FOR_VISIBLE_AND_CLICKABLE, context, locatorGroupData, locator, locatorPriority);
        }
        return actionResult;
    }

    //TODO environment should handled based on the profile
    public ActionResult performAction(ITestContext context, LocatorGroupData locatorGroupData, Object sku) throws Exception {
        JSONObject testData = getTestData(context);
        testData.put(TestDataUtils.Field.SKU.toString(), sku.toString());
        //TODO this should be handled in Coretest.java add Environment Locator to list
        //TODO similarly add Toolkit and EPPsso validation in CoreTest
        //performGlobalAction(context, locatorGroupData, sku, qaLocatorResultReports, qaResultReport);
        List<LocatorPriority> locatorsPriorities = locatorGroupData.getLocatorPriorityList();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        ReportUtils.logMessage(context, isDebug, "**===** Test data : " + testData);
        locatorsPriorities.stream().forEach(locatorPriority -> {
           //TODO FIX THIS
            TestLocator testLocator = null;
                    //getLocatorService().getLocatorById(new ObjectId(locatorPriority.getLocatorId()));
            try {
                // Perform action
                ActionResult actionResult = performPreAction(context, locatorGroupData, testLocator, locatorPriority);

                ReportUtils.logMessage(context, isDebug, "=== Processing : " + testLocator.getIdentifier());
                actionResult = performAction(context, locatorGroupData, testLocator, locatorPriority);
                if (actionResult.getStepStatus().equals(Status.FAIL)) {
                    String details = testLocator.getIdentifier() + " : " + " # Failed to " + testLocator.getMethodName() + ", Selector : " + testLocator.getUiLocatorSelector(TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
                    ReportUtils.logMessage(context, isDebug, "=== " + details);
                    //getQualityAssuranceService().saveErrorData(locatorPriority, testData, testLocator, details);
                    Media media = ReportUtils.info(context, details, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
                    //actionResult.setActionResult(testLocator.getIdentifier(), details, media != null ? media.getPath() : null, Status.FAIL);
                    throw new Exception(details);
                }
                performPostAction(context, locatorGroupData, testLocator, locatorPriority);
                //if iframe switch to default content
                if (BooleanUtils.isTrue(locatorPriority.getCheckIfIframe())) {
                    ReportUtils.logMessage(context, isDebug, "=== Iframe switchTo().defaultContent()");
                    threadTestContext.getDriver().switchTo().defaultContent();
                }
            } catch (Exception exp) {
                LOG.error("Exception - " + exp);
                ReportUtils.logMessage(context, isDebug, "=== ERROR: " + exp);
                String details = testLocator.getIdentifier() + " : " + " Failed to " + testLocator.getMethodName() + ", Selector : " + testLocator.getUiLocatorSelector(TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
                ReportUtils.logMessage(context, isDebug, "=== Info: " + details);
                Media media = ReportUtils.info(context, details, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
                ActionResult actionResult = new ActionResult();
                //actionResult.setActionResult(testLocator.getIdentifier(), details, media != null ? media.getPath() : null, Status.INFO);
                try {
                    if (locatorPriority.getCheckIfElementPresentOnThePage() == null || BooleanUtils.isNotTrue(locatorPriority.getCheckIfElementPresentOnThePage())) {
                        media = ReportUtils.info(context, testLocator.getIdentifier() + " : " + "Retry action started", testLocator.getIdentifier(), false);
                        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getIdentifier() + " : " + "Retry action started", media != null ? media.getPath() : null, Status.INFO);
                        ReportUtils.logMessage(context, isDebug, "=== Retry : " + testLocator.getIdentifier() + " : " + "Retry action started");
                        ReportUtils.logMessage(context, isDebug, "=== Wating: " + webDriverWaitTimeoutSeconds);
                        WaitUtils.waitForElementToVisible(threadTestContext, selectorService.getUiElement(context, testLocator), webDriverWaitTimeoutSeconds);
                        performAction(context, locatorGroupData, testLocator, locatorPriority);
                        ReportUtils.logMessage(context, isDebug, "=== Performed: " + testLocator.getIdentifier());
                    }
                } catch (Exception retryExp) {
                    ReportUtils.logMessage(context, isDebug, "=== Giving up: " + retryExp.getMessage());
                    ReportUtils.logMessage(context, isDebug, "=== Wating: " + webDriverWaitTimeoutSeconds);
                    if (isDebug) {
                        WebElement element = null;
                        reportAction(context, element, testLocator.getDescription() + " " + savePageSource(context, env.getProperty("server.url")), testLocator.getIdentifier(), false);
                    }
                    LOG.error("Exception - " + retryExp);
                    getQualityAssuranceService().saveErrorData(locatorPriority, testData, testLocator, StringUtils.EMPTY);
                    media = ReportUtils.fail(context, details, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
                    //actionResult.setActionResult(testLocator.getIdentifier(), details, media != null ? media.getPath() : null, Status.FAIL);
                    throw retryExp;
                }
            }
        });
        return null;
    }

//    private String getCampaign(JSONObject testData) {
//        String campaignId = TestDataUtils.getString(testData, TestDataUtils.Field.SHOP_CAMPAIGN);
//        if (StringUtils.isNotEmpty(campaignId)) {
//            Campaign campaign = campaignRepository.findByRecordId((campaignId));
//            return campaign != null ? campaign.getIdentifier() : null;
//        }
//        return null;
//    }


    private boolean performPostAction(ITestContext context, LocatorGroupData locatorGroupData, TestLocator locator, LocatorPriority locatorPriority) {
        // Check if enter key enabled if yes press enter key
        if (BooleanUtils.isTrue(locatorPriority.getEnterKey())) {
            performInternalPreAction(ActionType.ENTER_KEY_ACTION, context, locatorGroupData, locator, locatorPriority);
        }
        return true;
    }

    private ActionResult performInternalPreAction(String action, ITestContext context, LocatorGroupData locatorGroupData, TestLocator locator, LocatorPriority locatorPriority) {
        ElementActionService actionService = getActionService(action);
        //return actionService.performAction(context, locatorGroupData, locator, locatorPriority);
        return null;
    }

    private ActionResult performAction(ITestContext context, LocatorGroupData locatorGroupData, TestLocator testLocator, LocatorPriority locatorPriority) {
        ElementActionService actionService = getActionService(testLocator.getMethodName());

        if (BooleanUtils.isTrue(locatorPriority.getCheckIfElementPresentOnThePage())) {
            WebElement webElement = selectorService.getUiElement(context, testLocator);
            if (isElementPresentOnPage(webElement)) {
                //return actionService.performAction(context, locatorGroupData, testLocator, locatorPriority);
            }
        } else {
            //return actionService.performAction(context, locatorGroupData, testLocator, locatorPriority);
        }
        ActionResult actionResult = new ActionResult();
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.INFO);
        return actionResult;
    }
}
