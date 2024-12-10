package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import com.touchmind.qa.utils.TestDataUtils;
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