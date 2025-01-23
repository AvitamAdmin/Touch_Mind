package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.*;
import com.touchmind.core.mongo.repository.LocatorGroupFailedResultRepository;
import com.touchmind.core.mongo.repository.LocatorGroupRepository;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.touchmind.qa.utils.ReportUtils.reportAction;

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
        LocatorGroup locatorGroup = locatorGroupRepository.findByRecordId(groupId);
        AtomicReference<ActionResult> actionResult = new AtomicReference<>(new ActionResult());
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
                ActionRequest childRequest = new ActionRequest();
                TestLocator childTestLocator = BeanUtils.getLocatorService().getLocatorById(methodName);
                childRequest.setTestLocator(childTestLocator);
                ActionResult childResult = actionService.performAction(childRequest);
                if (childResult.getStepStatus().equals(Status.FAIL)) {
                    failCounter.getAndIncrement();
                }
            });
            QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
            qaLocatorResultReport.setQaTestResultId(actionRequest.getQaTestResultId());
            ITestContext context = actionRequest.getContext();
            WebElement element = null;
            Media media = reportAction(context, element, actionRequest.getTestLocator().getDescription(), actionRequest.getTestLocator().getIdentifier(), actionRequest.getLocatorGroupData().isTakeAScreenshot());
            Status status = failCounter.get() == locators.size() ? Status.FAIL : Status.PASS;
            actionResult.get().setStepStatus(status);
            if (status.equals(Status.FAIL)) {
                LocatorGroupFailedResult locatorGroupFailedResult = locatorGroupFailedResultRepository.findByGroupId(groupId);
                if (locatorGroupFailedResult == null) {
                    locatorGroupFailedResult = new LocatorGroupFailedResult();
                    locatorGroupFailedResult.setGroupId(groupId);

                    if (!locatorGroup.isApplyGlobally()) {
                        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
                        String cronSessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
                        locatorGroupFailedResult.setSessionId(cronSessionId);
                    }
                }
                //TODO Check if group id local or global LocatorGroup.applyGlobally if Global do not set sessionID
                //TODO else create new LocatorGroupFailedResult
            }
            qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), actionRequest.getTestLocator().getIdentifier(), actionRequest.getTestLocator().getDescription(), media != null ? media.getPath() : null, actionResult.get().getStepStatus(), null, ActionType.GROUP_ACTION);
            qaLocatorResultReportRepository.save(qaLocatorResultReport);
        }
        return actionResult.get();
    }
}
