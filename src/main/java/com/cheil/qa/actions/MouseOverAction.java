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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.MOUSE_OVER_ACTION)
public class MouseOverAction implements ElementActionService {
    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement element = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (element == null) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO);
        Actions action = new Actions(threadTestContext.getDriver());
        action.moveToElement(element).perform();
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
        return actionResult;
    }
}
