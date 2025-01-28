package com.touchmind.qa.strategies;

import com.aventstack.extentreports.Status;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.pages.concretepages.testPlans.AbstractTestPlan;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
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

    public ActionFactory(Map<String, ElementActionService> actionServiceMap) {
        this.actionServiceMap = actionServiceMap;
    }

    public static boolean isElementPresentOnPage(WebElement webElement) {
        return webElement != null;
    }

    public static boolean isElementPresentOnPage(ThreadTestContext context, WebElement webElement) {
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
            ActionResult actionResult = null;
            try {
                actionRequest.setLocatorPriority(locatorPriority);
                actionResult = performSelectorAction(actionRequest);
                if (actionResult.getStepStatus().equals(Status.FAIL)) {
                    throw new Exception();
                }
                //if iframe switch to default content
                if (BooleanUtils.isTrue(locatorPriority.getCheckIfIframe())) {
                    ReportUtils.logMessage(context, isDebug, "=== Iframe switchTo().defaultContent()");
                    threadTestContext.getDriver().switchTo().defaultContent();
                }
            } catch (Exception exp) {
                LOG.error("Exception - " + exp);
                try {
                    if (locatorPriority.getCheckIfElementPresentOnThePage() == null || BooleanUtils.isNotTrue(locatorPriority.getCheckIfElementPresentOnThePage())) {
                        ReportUtils.logMessage(context, isDebug, "=== Wating: " + webDriverWaitTimeoutSeconds);
                        actionResult = performSelectorAction(actionRequest);
                        assert actionResult != null;
                        if (actionResult.getStepStatus().equals(Status.FAIL)) {
                            ReportUtils.fail(context, actionResult.getMessage(), actionRequest.getTestLocator().getIdentifier(), true);
                            throw new Exception(actionResult.getMessage());
                        }
                    }
                } catch (Exception retryExp) {
                    ReportUtils.logMessage(context, isDebug, "=== Giving up: " + retryExp.getMessage());
                    ReportUtils.logMessage(context, isDebug, "=== Wating: " + webDriverWaitTimeoutSeconds);
                    if (isDebug) {
                        WebElement element = null;
                    }
                    LOG.error("Exception - " + retryExp);
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
            }
        }
        ElementActionService actionService = getActionService(methodName);
        actionRequest.setTestLocator(testLocator);
        if (BooleanUtils.isTrue(actionRequest.getLocatorPriority().getCheckIfElementPresentOnThePage())) {
            WebElement webElement = selectorService.getUiElement(actionRequest.getContext(), testLocator);
            if (isElementPresentOnPage(webElement)) {
                return actionService.performAction(actionRequest);
            }
        } else {
            return actionService.performAction(actionRequest);
        }
        return null;
    }
}
