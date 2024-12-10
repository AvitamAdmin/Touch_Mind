package com.cheil.qa.actions;

import com.cheil.core.mongo.model.TestLocator;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.strategies.ActionType;
import com.cheil.qa.utils.TestDataUtils;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.Set;

@Service(ActionType.SWITCH_TO_MAIN_PAGE)
public class SwitchToMainPageAction implements ElementActionService {

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        Set<String> numberOfWindows = threadTestContext.getDriver().getWindowHandles();
        threadTestContext.getDriver().switchTo().window(numberOfWindows.iterator().next());
        ActionResult actionResult = new ActionResult();
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.PASS);
        return actionResult;
    }
}
