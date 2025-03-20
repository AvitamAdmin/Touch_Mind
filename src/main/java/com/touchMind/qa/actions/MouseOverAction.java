package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.TestDataUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.MOUSE_OVER_ACTION)
public class MouseOverAction implements ElementActionService {
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement element = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        String description = null;
        if (testLocator != null) {
            JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
            String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        if (element == null) {
            actionResult.setMessage("Failed to find locator in the page - " + testLocator.getIdentifier() + ". Please check locator configuration:" + description);
            return actionResult;
        }
        Media media = reportAction(context, element, description, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.MOUSE_OVER_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        Actions action = new Actions(threadTestContext.getDriver());
        action.moveToElement(element).perform();
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
