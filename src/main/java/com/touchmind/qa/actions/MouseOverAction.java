package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import com.touchmind.qa.utils.TestDataUtils;
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
