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
import com.touchmind.qa.utils.TestDataUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import static com.touchmind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.REMOVE_ELEMENT_FROM_PAGE)
public class RemoveElementAction implements ElementActionService {

    public static final String JAVASCRIPT_REMOVE_ELEMENT = "arguments[0].remove();";
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
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (webElement == null) {
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, webElement, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.REMOVE_ELEMENT_FROM_PAGE);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        ((JavascriptExecutor) threadTestContext.getDriver()).executeScript(JAVASCRIPT_REMOVE_ELEMENT, webElement);
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
