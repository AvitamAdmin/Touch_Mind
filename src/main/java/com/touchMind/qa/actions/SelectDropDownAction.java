package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.SELECT_DROP_DOWN)
public class SelectDropDownAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        String description = null;
        if (testLocator != null) {
            JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
            String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        if (webElement == null) {
            actionResult.setMessage("Failed to find locator in the page - " + testLocator.getIdentifier() + ". Please check locator configuration:" + description);
            return actionResult;
        }
        Media media = reportAction(context, webElement, description, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.SELECT_DROP_DOWN);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        Select drpCountry = new Select(webElement);
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
        if (StringUtils.isNotEmpty(value)) {
            drpCountry.selectByValue(value);
        } else {
            drpCountry.selectByIndex(1);
        }
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
