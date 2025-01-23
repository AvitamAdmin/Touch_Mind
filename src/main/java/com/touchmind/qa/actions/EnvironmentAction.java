package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchmind.core.mongo.model.Environment;
import com.touchmind.core.mongo.model.EnvironmentConfig;
import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.repository.EnvironmentRepository;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.*;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.strategies.UrlFactory;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.ScrollUtils;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.qa.utils.WaitUtils;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.net.MalformedURLException;
import java.util.List;

import static com.touchmind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.ENVIRONMENT_ACTION)
public class EnvironmentAction implements ElementActionService {

    public static final String ERROR_PROCESSING_ENVIRONMENT_OBJECT_PLEASE_CHECK_THE_CONFIGURATIONS = "Error processing Environment object please check the configurations !";
    public static final String OPENED_MESSAGE = "%s\"%s\".";
    public static final String OPENED = "opened";
    public static final String URL_INFO_MESSAGE = "URL: \"%s\".";
    private final Logger LOG = LoggerFactory.getLogger(EnvironmentAction.class);
    @Value("${testng.webdriver.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;

    @Value("${qa.environment.mask.urls}")
    Boolean isMaskUrls;

    @Value("${qa.modheader.profile}")
    String qaModheaderProfile;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private UrlFactory urlFactory;
    @Autowired
    private UrlService urlService;
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QualityAssuranceService qualityAssuranceService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        ActionResult actionResult = new ActionResult();
        try {
            String environmentId = TestDataUtils.getString(testData, TestDataUtils.Field.ENVIRONMENT);
            Environment environment = environmentRepository.findByRecordId(environmentId);
            if (null != environment) {
                List<EnvironmentConfig> configs = environment.getConfigs();
                ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
                String sku = TestDataUtils.getString(testData, TestDataUtils.Field.SKU, threadTestContext.getTestIdentifier());
                if (CollectionUtils.isNotEmpty(configs)) {
                    configs.forEach(config -> {
                        performUrlAction(context, sku, testData, config, locatorGroupData.isTakeAScreenshot(), environment.getIdentifier());
                        performElementAction(context, config.getLoginNameUiSelector(), config.getLoginName(), config.getShortDescription(), locatorGroupData.isTakeAScreenshot());
                        performElementAction(context, config.getLoginPasswordSelector(), config.getLoginPassword(), config.getShortDescription(), locatorGroupData.isTakeAScreenshot());
                        performSubmitAction(context, config, locatorGroupData.isTakeAScreenshot());
                    });
                } else {
                    ReportUtils.fail(context, ERROR_PROCESSING_ENVIRONMENT_OBJECT_PLEASE_CHECK_THE_CONFIGURATIONS, StringUtils.EMPTY, false);
                    throw new Exception(ERROR_PROCESSING_ENVIRONMENT_OBJECT_PLEASE_CHECK_THE_CONFIGURATIONS);
                }
            }
        } catch (Exception e) {
            ReportUtils.logMessage(context, isDebug, "=== Environment Action : " + e.getMessage());
            String errorMsg = "Error processing Environment object please check the configurations !";
            ReportUtils.fail(context, errorMsg, StringUtils.EMPTY, false);
            LOG.error("errorMsg :" + e.getMessage());
            ReportUtils.logMessage(context, isDebug, "=== Environment Error message save  : " + errorMsg);
            qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }
        //Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, Status.INFO, null, ActionType.ENVIRONMENT_ACTION);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }

    private boolean performUrlAction(ITestContext context, Object sku, JSONObject testData, EnvironmentConfig config, boolean takeAScreenshot, String identifier) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        String subsidiary = TestDataUtils.getString(testData, TestDataUtils.Field.SUBSIDIARY);
        String actionUrl = config.getUrl();
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testData, TestDataUtils.Field.IS_DEBUG));
        if (StringUtils.isNotEmpty(actionUrl)) {
            String url = null;
            try {
                if (BooleanUtils.isTrue(config.getWaitBeforeUrl())) {
                    Thread.sleep(webDriverWaitTimeoutSeconds * 1000L);
                }
                url = urlFactory.constructUrl(context, urlService.getUrlServiceType(subsidiary), ObjectUtils.isNotEmpty(sku) ? sku.toString() : "", actionUrl);

                if (StringUtils.isNotEmpty(url)) {
                    ReportUtils.info(context, String.format(URL_INFO_MESSAGE, url), StringUtils.EMPTY, false);
                    //ReportUtils.info(context, String.format(URL_INFO_MESSAGE, ReportUtils.getMaskedString(url, isMaskUrls)), StringUtils.EMPTY, false);
                    /*
                    if (StringUtils.isNotEmpty(identifier) && "prod_qaweb".equals(identifier) && StringUtils.isNotEmpty(qaModheaderProfile)) {
                        threadTestContext.getDriver().get(qaModheaderProfile);
                        LOG.info("######################################");
                        LOG.info(threadTestContext.getDriver().toString());
                        LOG.info("######################################");
                    }
                     */
                    threadTestContext.getDriver().get(url);
                    ReportUtils.info(context, String.format(OPENED_MESSAGE, BeanUtils.getLocalizedString(OPENED), config.getShortDescription() + " : " + url), StringUtils.EMPTY, takeAScreenshot);
                    //ReportUtils.info(context, String.format(OPENED_MESSAGE, threadTestContext.getLocalizedString(OPENED), config.getShortDescription() + " : " + ReportUtils.getMaskedString(url, isMaskUrls)), StringUtils.EMPTY, takeAScreenshot);
                }
                if (BooleanUtils.isTrue(config.getWaitAfterUrl())) {
                    Thread.sleep(webDriverWaitTimeoutSeconds * 1000L);
                }
            } catch (MalformedURLException e) {
                ReportUtils.logMessage(context, isDebug, "=== Environment URL Action : " + e.getMessage());
                String errorMsg = "Error processing Environment object please check the configurations !" + e.getMessage();
                LOG.error(errorMsg, e);
                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                ReportUtils.logMessage(context, isDebug, "=== Environment Action Url error : " + errorMsg + " Test data : " + testData);
                return false;
            } catch (InterruptedException e) {
                ReportUtils.logMessage(context, isDebug, "=== Environment URL Action InterruptedException : " + e.getMessage());
                String errorMsg = "Error processing Environment object please check the configurations !" + e.getMessage();
                LOG.error(errorMsg, e);
                qualityAssuranceService.saveErrorData(null, testData, null, errorMsg);
                ReportUtils.logMessage(context, isDebug, "=== Environment Action Url error InterruptedException : " + errorMsg + " Test data : " + testData);
                return false;
            }
        }
        LOG.info("Action: " + config.getShortDescription());
        return true;
    }

    private void performElementAction(ITestContext context, String uiSelector, String inputData, String shortDescription, boolean takeAScreenshot) {
        if (StringUtils.isEmpty(inputData)) return;

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        ReportUtils.info(context, String.format(OPENED_MESSAGE, BeanUtils.getLocalizedString(uiSelector), shortDescription), StringUtils.EMPTY, false);
        LocatorSelectorDto locatorSelectorDto = new LocatorSelectorDto();
        locatorSelectorDto.setXpathSelector(uiSelector);
        WebElement element = selectorService.getBy(threadTestContext, locatorSelectorDto);
        if (element == null) {
            ReportUtils.info(context, String.format(OPENED_MESSAGE, BeanUtils.getLocalizedString(uiSelector), shortDescription), StringUtils.EMPTY, takeAScreenshot);
            return;
        }
        element.clear();
        element.sendKeys(inputData);
    }

    private void performSubmitAction(ITestContext context, EnvironmentConfig config, boolean takeAScreenshot) {
        if (StringUtils.isEmpty(config.getActionElement())) return;

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        LocatorSelectorDto locatorSelectorDto = new LocatorSelectorDto();
        locatorSelectorDto.setXpathSelector(config.getActionElement());
        WebElement element = selectorService.getBy(threadTestContext, locatorSelectorDto);
        if (element == null) {
            ReportUtils.info(context, String.format(OPENED_MESSAGE, BeanUtils.getLocalizedString(config.getActionElement()), config.getShortDescription()), StringUtils.EMPTY, takeAScreenshot);
            return;
        }
        ReportUtils.info(context, String.format(OPENED_MESSAGE, BeanUtils.getLocalizedString(config.getActionElement()), config.getShortDescription()), StringUtils.EMPTY, takeAScreenshot);
        if (BooleanUtils.isTrue(config.getWaitBeforeClick())) {
            WaitUtils.wait(threadTestContext, webDriverWaitTimeoutSeconds);
        }
        ScrollUtils.scrollIntoView(threadTestContext, element);
        element.click();
        if (BooleanUtils.isTrue(config.getWaitAfterClick())) {
            WaitUtils.wait(threadTestContext, webDriverWaitTimeoutSeconds);
        }
    }
}
