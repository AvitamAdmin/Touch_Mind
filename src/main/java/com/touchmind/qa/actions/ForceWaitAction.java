package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
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

import static com.touchmind.qa.utils.ReportUtils.reportAction;

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
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.PASS);
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.FORCE_WAIT_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }
}
