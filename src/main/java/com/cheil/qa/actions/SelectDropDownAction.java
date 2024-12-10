package com.cheil.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.form.LocatorGroupData;
import com.cheil.form.LocatorSelectorDto;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.service.SelectorService;
import com.cheil.qa.strategies.ActionType;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.SELECT_DROP_DOWN)
public class SelectDropDownAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (webElement == null) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }
        Media media = reportAction(context, webElement, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        Select drpCountry = new Select(webElement);
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
        if (StringUtils.isNotEmpty(value)) {
            drpCountry.selectByValue(value);
        } else {
            drpCountry.selectByIndex(1);
        }
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
        return actionResult;
    }
}
