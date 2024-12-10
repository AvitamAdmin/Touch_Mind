package com.cheil.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.cheil.core.mongo.model.QaLocatorResultReport;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.mongo.repository.QaLocatorResultReportRepository;
import com.cheil.form.LocatorGroupData;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.service.SelectorService;
import com.cheil.qa.strategies.ActionType;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.ScrollUtils;
import com.cheil.qa.utils.TestDataUtils;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

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
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(),testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO,null);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        ScrollUtils.scrollIntoView(threadTestContext, element);
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(),testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS,null);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        element.click();
        return actionResult;
    }
}
