package com.touchmind.qa.actions;

import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.TestDataUtils;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.Set;
import java.util.stream.Collectors;

@Service(ActionType.WAIT_FOR_POPUP_AND_SWITCH)
public class WaitForPopupAndSwitchAction implements ElementActionService {

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        Set<String> numberOfWindows = threadTestContext.getDriver().getWindowHandles();
        threadTestContext.getFluentWait().until(driver -> numberOfWindows.size() > 1);
        threadTestContext.getDriver().switchTo().window(numberOfWindows.stream().collect(Collectors.toList()).get(1));
        ActionResult actionResult = new ActionResult();
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.PASS);
        return actionResult;
    }
}
