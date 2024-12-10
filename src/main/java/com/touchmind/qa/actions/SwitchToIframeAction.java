package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchmind.core.mongo.model.TestLocator;
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

@Service(ActionType.SWITCH_TO_IFRAME)
public class SwitchToIframeAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (webElement == null) {
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }
        WebElement iframe = webElement;
        threadTestContext.getDriver().switchTo().frame(iframe);
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.PASS);
        return actionResult;
    }
}
