package com.cheil.qa.actions;

import com.cheil.core.mongo.model.TestLocator;
import com.cheil.form.LocatorGroupData;
import com.cheil.form.LocatorSelectorDto;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.service.SelectorService;
import com.cheil.qa.strategies.ActionType;
import com.cheil.qa.utils.ReportUtils;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.Map;

@Service(ActionType.CASCADE_TEST_PLAN_ACTION)
public class CascadeAction implements ElementActionService {

    public static final String INNER_HTML = "innerHTML";
    public static final String J_QUERY_ARGUMENTS_0_VAL = "return jQuery(arguments[0]).val()";
    @Autowired
    private SelectorService selectorService;

    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement element = selectorService.getUiElement(context, testLocator);
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        ActionResult actionResult = new ActionResult();
        if (element == null) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }
        reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String locator = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
        
        Map<String, String> mapData = threadTestContext.getData();
        String elementTxt = element.getAttribute(INNER_HTML);
        String data = StringUtils.isNotEmpty(elementTxt) ? elementTxt : element.getText();
        if (StringUtils.isEmpty(data)) {
            JavascriptExecutor js = (JavascriptExecutor) element;
            data = (String) js.executeScript(J_QUERY_ARGUMENTS_0_VAL);
        }
        mapData.put(locator, data);
        testData.put(testLocator.getIdentifier(), data);
        ReportUtils.info(context, "Data captured for capture action: " + data, testLocator.getIdentifier(), false);
        //actionResult.setActionResult(testLocator.getIdentifier(), data, null, Status.INFO);
        return actionResult;
    }
}
