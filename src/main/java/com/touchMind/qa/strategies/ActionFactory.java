package com.touchMind.qa.strategies;

import com.aventstack.extentreports.Status;
import com.touchMind.core.mongo.dto.LocatorGroupDto;
import com.touchMind.core.mongo.model.LocatorGroupFailedResult;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.LocatorGroupFailedResultRepository;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.pages.concretepages.testPlans.AbstractTestPlan;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.utils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private SelectorService selectorService;
    @Autowired
    private LocatorGroupFailedResultRepository locatorGroupFailedResultRepository;
    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

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

    public ActionResult performAction(ActionRequest actionRequest) throws Exception {
        ITestContext context = actionRequest.getContext();
        JSONObject testData = getTestData(context);
        testData.put(TestDataUtils.Field.SKU.toString(), actionRequest.getSku().toString());
        List<LocatorPriority> locatorsPriorities = actionRequest.getLocatorGroupData().getLocatorPriorityList();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        ReportUtils.logMessage(context, isDebug, "**===** Test data : " + testData);
        for (LocatorPriority locatorPriority : locatorsPriorities) {
            try {
                boolean runTest = true;
                if (StringUtils.isNotEmpty(locatorPriority.getGroupId())) {
                    String cronSessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
                    LocatorGroupFailedResult locatorGroupFailedResult = locatorGroupFailedResultRepository.findByGroupId(locatorPriority.getGroupId());
                    if (locatorGroupFailedResult != null) {
                        if (StringUtils.isNotEmpty(locatorGroupFailedResult.getSessionId()) && cronSessionId.equalsIgnoreCase(locatorGroupFailedResult.getSessionId())) {
                            runTest = false;
                        }
                        LocatorGroupDto locatorGroupDto = locatorPriority.getLocatorGroup();
                        if (locatorGroupDto != null && locatorGroupDto.isClearHardFailed()) {
                            if (StringUtils.isEmpty(locatorGroupFailedResult.getSessionId())) {
                                locatorGroupFailedResultRepository.deleteByGroupId(locatorPriority.getGroupId());
                            }
                            runTest = true;
                        }
                    }
                }
                if (runTest) {
                    actionRequest.setLocatorPriority(locatorPriority);
                    ActionResult actionResult = performSelectorAction(actionRequest);
                    if (actionResult.getStepStatus().equals(Status.FAIL)) {
                        throw new Exception(actionResult.getMessage());
                    }
                    //if iframe switch to default content
                    if (BooleanUtils.isTrue(locatorPriority.getCheckIfIframe())) {
                        ReportUtils.logMessage(context, isDebug, "=== Iframe switchTo().defaultContent()");
                        threadTestContext.getDriver().switchTo().defaultContent();
                    }
                } else {
                    ReportUtils.logMessage(context, isDebug, "Skipping child test run as parent failed for group id" + locatorPriority.getGroupId());
                }
            } catch (Exception exp) {
                try {
                    if (locatorPriority.getCheckIfElementPresentOnThePage() == null || BooleanUtils.isNotTrue(locatorPriority.getCheckIfElementPresentOnThePage())) {
                        //ReportUtils.logMessage(context, isDebug, "=== Wating: " + webDriverWaitTimeoutSeconds);
                        ActionResult actionResult = performSelectorAction(actionRequest);
                        if (actionResult.getStepStatus().equals(Status.FAIL)) {
                            throw new Exception(actionResult.getMessage());
                        }
                    }
                } catch (Exception retryExp) {
                    //ReportUtils.logMessage(context, isDebug, "=== Giving up: " + retryExp.getMessage());
                    //ReportUtils.logMessage(context, isDebug, "=== Waiting: " + webDriverWaitTimeoutSeconds);
                    ReportUtils.fail(context, retryExp.getMessage(), locatorPriority.getLocatorName(), actionRequest.getLocatorGroupData().isTakeAScreenshot());
                    //LOG.error("Exception after retry - " + retryExp);
                    throw retryExp;
                }
            }
        }
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }

    private ActionResult performSelectorAction(ActionRequest actionRequest) {
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        String methodName = locatorPriority.getLocatorId();
        TestLocator testLocator = null;
        if (StringUtils.isNotEmpty(locatorPriority.getGroupId())) {
            methodName = ActionType.GROUP_ACTION;
            actionRequest.setActionServiceMap(actionServiceMap);
        } else {
            testLocator = BeanUtils.getLocatorService().getLocatorById(methodName);
            if (testLocator != null) {
                methodName = testLocator.getMethodName();
            } /*else if (methodName.matches("[0-9]+")) {
                LOG.info("Locator has been deleted. ignoring the run");
                TestLocatorGroup testLocatorGroup = testLocatorGroupRepository.findByIdentifier(actionRequest.getTestCaseId());
                String finalMethodName = methodName;
                List<LocatorPriority> locatorPriorityList = testLocatorGroup.getTestLocators().stream().filter(locatorPriority1 -> locatorPriority1 != null &&
                        StringUtils.isNotEmpty(locatorPriority1.getLocatorId()) && locatorPriority1.getLocatorId().equalsIgnoreCase(finalMethodName)).toList();
                if (CollectionUtils.isNotEmpty(locatorPriorityList)) {
                    testLocatorGroup.setTestLocators(testLocatorGroup.getTestLocators().stream().filter(locatorPriority1 -> locatorPriority1 != null
                            && StringUtils.isNotEmpty(locatorPriority1.getLocatorId()) && !locatorPriority1.getLocatorId().equalsIgnoreCase(finalMethodName)).toList());
                    testLocatorGroupRepository.save(testLocatorGroup);
                    ActionResult actionResult = new ActionResult();
                    actionResult.setStepStatus(Status.PASS);
                    return actionResult;
                }
            }*/
        }
        actionRequest.setTestLocator(testLocator);
        if (BooleanUtils.isTrue(actionRequest.getLocatorPriority().getWaitForElementVisibleAndClickable())) {
            getActionService("WaitForVisibleAndClickable").performAction(actionRequest);
        }
        ElementActionService actionService = getActionService(methodName);
        if (BooleanUtils.isTrue(actionRequest.getLocatorPriority().getCheckIfElementPresentOnThePage())) {
            WebElement webElement = selectorService.getUiElement(actionRequest.getContext(), testLocator);
            if (isElementPresentOnPage(webElement)) {
                return actionService.performAction(actionRequest);
            }
        } else {
            return actionService.performAction(actionRequest);
        }
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        actionResult.setMessage("Locator failed - " + actionRequest.getTestLocator().getIdentifier());
        return actionResult;
    }
}
