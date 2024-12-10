package com.touchmind.qa.actions;

import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.FORCE_WAIT_ACTION)
public class ForceWaitAction implements ElementActionService {

    @Value("${testng.webdriver.explicit.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    Logger LOG = LoggerFactory.getLogger(ForceWaitAction.class);

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));

        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
        String inputValue = null;
        if (BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
            inputValue = threadTestContext.getData().get(value);
        } else {
            inputValue = value;
        }

        try {
            if (StringUtils.isNotEmpty(inputValue) && NumberUtils.isCreatable(inputValue)) {
                int waitTime = webDriverWaitTimeoutSeconds;
                try {
                    waitTime = Integer.parseInt(inputValue);
                } catch (Exception e) {
                    LOG.error("Error in force wait : " + e);
                }
                Thread.sleep(waitTime * 1000L);
            } else {
                Thread.sleep(webDriverWaitTimeoutSeconds * 1000L);
            }
        } catch (InterruptedException e) {
            ReportUtils.logMessage(context, isDebug, "=== Error in force wait : " + e.getMessage());
            LOG.error("Error in force wait : " + e);
        }
        ActionResult actionResult = new ActionResult();
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.PASS);
        return actionResult;
    }
}
