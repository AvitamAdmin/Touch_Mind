package com.cheil.qa.actions;

import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.strategies.ActionType;
import com.cheil.qa.utils.TestDataUtils;
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
