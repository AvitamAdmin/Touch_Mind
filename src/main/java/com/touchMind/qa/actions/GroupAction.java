package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchMind.core.mongo.model.LocatorGroup;
import com.touchMind.core.mongo.model.LocatorGroupFailedResult;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.LocatorGroupFailedResultRepository;
import com.touchMind.core.mongo.repository.LocatorGroupRepository;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.utils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service(ActionType.GROUP_ACTION)
public class GroupAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Autowired
    private LocatorGroupRepository locatorGroupRepository;

    @Autowired
    private LocatorGroupFailedResultRepository locatorGroupFailedResultRepository;

    public ActionResult performAction(ActionRequest actionRequest) {
        String groupId = actionRequest.getLocatorPriority().getGroupId();
        LocatorGroup locatorGroup = locatorGroupRepository.findByIdentifier(groupId);
        ActionResult actionResultObj = new ActionResult();
        actionResultObj.setStepStatus(Status.FAIL);
        ITestContext context = actionRequest.getContext();
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());

        AtomicReference<ActionResult> actionResult = new AtomicReference<>(actionResultObj);
        if (null != locatorGroup && CollectionUtils.isNotEmpty(locatorGroup.getTestLocators())) {
            List<LocatorPriority> locators = locatorGroup.getTestLocators();
            AtomicInteger failCounter = new AtomicInteger();
            locators.stream().forEach(locator -> {
                TestLocator testLocator = BeanUtils.getLocatorService().getLocatorById(locator.getLocatorId());
                String methodName = testLocator.getMethodName();
                ElementActionService actionService = actionRequest.getActionServiceMap().get(methodName);
                if (actionService == null) {
                    throw new RuntimeException("Unsupported action type");
                }
                actionRequest.setTestLocator(testLocator);
                ActionResult childResult = actionService.performAction(actionRequest);
                if (childResult.getStepStatus().equals(Status.FAIL)) {
                    String description = null;
                    String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
                    description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
                    ReportUtils.warn(actionRequest.getContext(), description, testLocator.getIdentifier(), actionRequest.getLocatorGroupData().isTakeAScreenshot());
                    failCounter.getAndIncrement();
                }
            });
            QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
            qaLocatorResultReport.setQaTestResultId(actionRequest.getQaTestResultId());
            //WebElement element = null;
            //Media media = reportAction(context, element, testLocator.getShortDescription(), testLocator.getIdentifier(), actionRequest.getLocatorGroupData().isTakeAScreenshot(), testLocator);
            Status status = failCounter.get() == locators.size() ? Status.FAIL : Status.PASS;
            actionResult.get().setStepStatus(status);
            if (status.equals(Status.FAIL)) {
                ReportUtils.fail(actionRequest.getContext(), "Group action failed for:" + locatorGroup.getShortDescription(), locatorGroup.getIdentifier(), actionRequest.getLocatorGroupData().isTakeAScreenshot());
                String cronSessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
                LocatorGroupFailedResult locatorGroupFailedResult = locatorGroupFailedResultRepository.findByGroupId(groupId);
                if (locatorGroupFailedResult == null || StringUtils.isNotEmpty(locatorGroupFailedResult.getSessionId())) {
                    locatorGroupFailedResult = new LocatorGroupFailedResult();
                    locatorGroupFailedResult.setGroupId(groupId);
                }
                if (!locatorGroup.isApplyGlobally()) {
                    locatorGroupFailedResult.setSessionId(cronSessionId);
                }
                locatorGroupFailedResultRepository.save(locatorGroupFailedResult);
            }
            //qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), actionRequest.getTestLocator().getIdentifier(), actionRequest.getTestLocator().getShortDescription(), media != null ? media.getPath() : null, actionResult.get().getStepStatus(), null, ActionType.GROUP_ACTION);
            //qaLocatorResultReportRepository.save(qaLocatorResultReport);
        }
        return actionResult.get();
    }
}
