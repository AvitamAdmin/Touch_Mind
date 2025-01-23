package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ScrollUtils;
import com.touchmind.qa.utils.TestDataUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import static com.touchmind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.CLICK_ACTION)
public class ClickAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) actionRequest.getContext().getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement element = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (element == null) {
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.CLICK_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        ScrollUtils.scrollIntoView(threadTestContext, element);
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS, null, ActionType.CLICK_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        element.click();
        actionResult.setStepStatus(Status.INFO);
        return actionResult;
    }
}
