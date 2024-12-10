package com.cheil.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.form.LocatorGroupData;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.service.SelectorService;
import com.cheil.qa.strategies.ActionType;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.REMOVE_ELEMENT_FROM_PAGE)
public class RemoveElementAction implements ElementActionService {

    public static final String JAVASCRIPT_REMOVE_ELEMENT = "arguments[0].remove();";
    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (webElement == null) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, webElement, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        ((JavascriptExecutor) threadTestContext.getDriver()).executeScript(JAVASCRIPT_REMOVE_ELEMENT, webElement);
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
        return actionResult;
    }
}
