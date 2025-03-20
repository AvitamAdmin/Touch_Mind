package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;

import static com.touchMind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchMind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;

@Service(ActionType.OPEN_URL_ACTION)
public class OpenUrlAction implements ElementActionService {

    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String url = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
        Media media = ReportUtils.info(context,
                testLocator.getShortDescription() +
                        COLON_SPACE_QUOTES +
                        url +
                        QUOTES_DOT_SPACE, testLocator.getIdentifier(),
                locatorGroupData.isTakeAScreenshot());
        //String url = testLocator.getUiLocatorSelector(itemSite).getXpathSelector();
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        if (StringUtils.isEmpty(url)) {
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }
        Map<String, String> params = new HashMap<>();
        params.put("temSite", TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE));
        url = StringSubstitutor.replace(url, params, "$[", "]");
        threadTestContext.getDriver().get(url);
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.OPEN_URL_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
