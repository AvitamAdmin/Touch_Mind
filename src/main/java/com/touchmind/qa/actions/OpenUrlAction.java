package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import static com.touchmind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchmind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;

@Service(ActionType.OPEN_URL_ACTION)
public class OpenUrlAction implements ElementActionService {

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String inputData = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
        Media media = ReportUtils.info(context,
                testLocator.getDescription() +
                        COLON_SPACE_QUOTES +
                        inputData +
                        QUOTES_DOT_SPACE, testLocator.getIdentifier(),
                locatorGroupData.isTakeAScreenshot());
        String url = testLocator.getUiLocatorSelector(itemSite).getXpathSelector();
        ActionResult actionResult = new ActionResult();
        if (StringUtils.isEmpty(url)) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }

        Map<String, String> params = new HashMap<>();
        params.put("temSite", TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
        url = StringSubstitutor.replace(url, params, "$[", "]");
        threadTestContext.getDriver().get(url);
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data: " + testLocator.getInputDataEncrypted(itemSite), media != null ? media.getPath() : null, Status.PASS);
        return actionResult;
    }
}
