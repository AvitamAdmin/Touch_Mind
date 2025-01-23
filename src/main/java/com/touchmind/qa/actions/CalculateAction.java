package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.touchmind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchmind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;

@Service(ActionType.CALCULATE_ACTION)
public class CalculateAction implements ElementActionService {

    Logger logger = LoggerFactory.getLogger(CalculateAction.class);
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private TestLocatorRepository testLocatorRepository;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

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
        String inputText = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext expContext = new StandardEvaluationContext();
        if (CollectionUtils.isNotEmpty(testLocator.getSubLocators()) && StringUtils.isNotEmpty(inputText)) {
            List<BigDecimal> numericValues = new ArrayList<>();
            for (String subLocator : testLocator.getSubLocators()) {
                String value = String.valueOf(testData.get(subLocator));
                String numericVal = value.replaceAll("[^0-9]", "");
                if (StringUtils.isNotEmpty(numericVal)) {
                    logger.info("numericVal" + numericVal + " - subLocator" + subLocator);
                    //numericValues.add(BigDecimal.valueOf(Long.parseLong(numericVal)));
                    expContext.setVariable(subLocator, Long.valueOf(numericVal));
                }
            }
            boolean result2 = Boolean.TRUE.equals(parser.parseExpression(testLocator.getExpression()).getValue(expContext, Boolean.class));
            actionResult.setStepStatus(result2 ? Status.PASS : Status.FAIL);
        }
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.CALCULATE_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }
}
