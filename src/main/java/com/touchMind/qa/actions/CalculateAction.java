package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.FileHandler;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.core.mongo.repository.TestLocatorRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
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

import static com.touchMind.qa.utils.WaitUtils.COLON_SPACE_QUOTES;
import static com.touchMind.qa.utils.WaitUtils.QUOTES_DOT_SPACE;

@Service(ActionType.CALCULATE_ACTION)
public class CalculateAction implements ElementActionService {

    Logger logger = LoggerFactory.getLogger(FileHandler.class);
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
        actionResult.setStepStatus(Status.FAIL);
        Media media = ReportUtils.info(context,
                testLocator.getShortDescription() +
                        COLON_SPACE_QUOTES +
                        testLocator.getInputDataEncrypted(itemSite) +
                        QUOTES_DOT_SPACE, testLocator.getIdentifier(),
                locatorGroupData.isTakeAScreenshot());
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getShortDescription() + " Data: " + testLocator.getInputDataEncrypted(itemSite), media.getPath(), Status.FAIL);

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext expContext = new StandardEvaluationContext();
        if (CollectionUtils.isNotEmpty(testLocator.getSubLocators())) {
            for (String subLocator : testLocator.getSubLocators()) {
                TestLocator locator = testLocatorRepository.findByIdentifier(subLocator);
                String value = String.valueOf(testData.get(locator.getIdentifier()));
                String numericVal = value.replaceAll("[^0-9]", "");
                if (StringUtils.isNotEmpty(numericVal)) {
                    logger.info("numericVal" + numericVal + " - subLocator" + locator.getIdentifier());
                    ReportUtils.info(context,
                            testLocator.getShortDescription() +
                                    COLON_SPACE_QUOTES +
                                    numericVal + ", Expression is : " + testLocator.getExpression() +
                                    QUOTES_DOT_SPACE, locator.getIdentifier(),
                            locatorGroupData.isTakeAScreenshot());
                    //numericValues.add(BigDecimal.valueOf(Long.parseLong(numericVal)));
                    expContext.setVariable(locator.getIdentifier(), Long.valueOf(numericVal));
                }
            }
            boolean result2 = Boolean.TRUE.equals(parser.parseExpression(testLocator.getExpression()).getValue(expContext, Boolean.class));
            actionResult.setStepStatus(result2 ? Status.PASS : Status.FAIL);
        }
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.CALCULATE_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }
}
