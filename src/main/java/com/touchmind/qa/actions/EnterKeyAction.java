package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.ENTER_KEY_ACTION)
public class EnterKeyAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement element = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (element == null) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Element ar not found", null, Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        element.clear();
        element.sendKeys(Keys.ENTER);
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
        return actionResult;
    }
}
