package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.FileHandler;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.TestLocatorRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import static com.touchmind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchmind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.CALCULATE_ACTION)
public class CalculateAction implements ElementActionService {

    Logger logger = LoggerFactory.getLogger(FileHandler.class);
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private TestLocatorRepository testLocatorRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        ActionResult actionResult = new ActionResult();
        Media media = ReportUtils.info(context,
                testLocator.getDescription() +
                        COLON_SPACE_QUOTES +
                        testLocator.getInputDataEncrypted(itemSite) +
                        QUOTES_DOT_SPACE, testLocator.getIdentifier(),
                locatorGroupData.isTakeAScreenshot());
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data: " + testLocator.getInputDataEncrypted(itemSite), media.getPath(), Status.FAIL);

        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String inputText = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
        //TODO move it to new implementation
        /*
        if (CollectionUtils.isNotEmpty(testLocator.getSubLocators()) && StringUtils.isNotEmpty(inputText)) {
            List<BigDecimal> numericValues = new ArrayList<>();
            for (String subLocator : testLocator.getSubLocators()) {
                String value = String.valueOf(testData.get(subLocator));
                String numericVal = value.replaceAll("[^0-9]", "");
                if (StringUtils.isNotEmpty(numericVal)) {
                    logger.info("numericVal" + numericVal + " - subLocator" + subLocator);
                    numericValues.add(BigDecimal.valueOf(Long.parseLong(numericVal)));
                }
            }
            if (CollectionUtils.isNotEmpty(numericValues)) {
                BigDecimal valueToCompare = BigDecimal.valueOf(Long.parseLong(inputText));
                BigDecimal beforeRedemption = numericValues.get(0);
                if (numericValues.size() == 1) {
                    if (valueToCompare.compareTo(beforeRedemption) <= 0) {
                        actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data: " + testLocator.getInputDataEncrypted(itemSite), media.getPath(), Status.PASS);
                    }
                } else if (numericValues.size() == 3) {
                    BigDecimal afterRedemption = numericValues.get(1);
                    BigDecimal finalValueToCompare = numericValues.get(2).subtract(valueToCompare).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal total = beforeRedemption.subtract(afterRedemption).setScale(2, RoundingMode.HALF_UP);
                    if (finalValueToCompare.compareTo(total) > 0) {
                        actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data: " + testLocator.getInputDataEncrypted(itemSite), media.getPath(), Status.PASS);
                    }
                }
            }
        }

         */
        return actionResult;
    }
}
