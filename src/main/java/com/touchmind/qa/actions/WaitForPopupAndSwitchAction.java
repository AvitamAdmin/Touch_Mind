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
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.Set;
import java.util.stream.Collectors;

import static com.touchmind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.WAIT_FOR_POPUP_AND_SWITCH)
public class WaitForPopupAndSwitchAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement element = selectorService.getUiElement(context, testLocator);
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        Set<String> numberOfWindows = threadTestContext.getDriver().getWindowHandles();
        threadTestContext.getFluentWait().until(driver -> numberOfWindows.size() > 1);
        threadTestContext.getDriver().switchTo().window(numberOfWindows.stream().collect(Collectors.toList()).get(1));
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.WAIT_FOR_POPUP_AND_SWITCH);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
