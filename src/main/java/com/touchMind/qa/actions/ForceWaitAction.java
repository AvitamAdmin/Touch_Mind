package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.FORCE_WAIT_ACTION)
public class ForceWaitAction implements ElementActionService {

    @Value("${testng.webdriver.explicit.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    Logger LOG = LoggerFactory.getLogger(ForceWaitAction.class);
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;
    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement element = selectorService.getUiElement(context, testLocator);
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));

        String description = null;
        if (testLocator != null) {
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
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
        Media media = reportAction(context, element, description, testLocator.getIdentifier() + ", Input data:  " + inputValue, locatorGroupData.isTakeAScreenshot());
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.PASS);
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.FORCE_WAIT_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }
}
