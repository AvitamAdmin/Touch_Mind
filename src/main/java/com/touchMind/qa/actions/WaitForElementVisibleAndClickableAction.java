package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.utils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.touchMind.qa.utils.ReportUtils.reportAction;


@Service("WaitForVisibleAndClickable")
public class WaitForElementVisibleAndClickableAction implements ElementActionService {
    public static final String WAITING_FOR_THE_FOLLOWING_ELEMENTS = "waiting.for.the.following.elements";
    public static final String DELIMITER = ", ";
    @Value("${testng.webdriver.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    /**
     * Waits for any of the given elements to meet specified conditions and returns the index of the first one that does.
     * This method uses asynchronous execution to wait for elements defined by 'locators', applying the given
     * 'conditionsBuilder' to each locator. It returns the index (starting from 0) of the first locator whose corresponding
     * element meets the conditions. This index is compatible with the order of the 'locators' array.
     *
     * @param context           The thread context in which the wait operation is executed.
     * @param conditionsBuilder A function that defines the conditions to be applied to each locator.
     * @param locators          An array of locators for which conditions are checked.
     * @return The index of the first locator whose corresponding element meets the conditions.
     */
    public static int waitForElements(ITestContext context, Function<By, List<ExpectedCondition<?>>> conditionsBuilder, By... locators) {
        try {
            ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
            ReportUtils.info(context,
                    BeanUtils.getLocalizedString(WAITING_FOR_THE_FOLLOWING_ELEMENTS) + convertToString(locators),
                    StringUtils.EMPTY, false);

            List<CompletableFuture<Integer>> futures = IntStream.range(0, locators.length)
                    .mapToObj(index -> CompletableFuture.supplyAsync(() -> {
                        threadTestContext.getFluentWait().until(driver -> {
                            List<ExpectedCondition<?>> conditions = conditionsBuilder.apply(locators[index]);
                            return conditions.stream().allMatch(cond -> cond.apply(driver) != null);
                        });
                        return index;
                    })).collect(Collectors.toList());

            CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]));
            int detectedIndex = (Integer) anyOfFuture.get();

            // Cancel all other futures
            futures.forEach(future -> {
                if (!future.isDone()) {
                    future.cancel(true);
                }
            });

            return detectedIndex;
        } catch (Exception e) {
            /*
            Wrapping checked exceptions into the unchecked one to avoid having to use "throws" in method signatures.
            TestNG listener will take care of properly logging all exceptions in the report.
            */
            throw new RuntimeException(e);
        }
    }

    private static String convertToString(By... locators) {
        return String.join(DELIMITER, Arrays.stream(locators)
                .map(By::toString)
                .collect(Collectors.toList()));
    }

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        WebElement element = selectorService.getUiElement(context, testLocator);
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        if (webElement == null) {
            threadTestContext.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(webDriverWaitTimeoutSeconds));
            webElement = selectorService.getUiElement(context, testLocator);
        }
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        String description = null;
        if (testLocator != null) {
            JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
            String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        if (BooleanUtils.isTrue(locatorPriority.getCheckIfElementPresentOnThePage()) && null == webElement) {
            actionResult.setMessage("Failed to find locator in the page - " + testLocator.getIdentifier() + ". Please check locator configuration:" + description);
            return actionResult;
        }
        conditionsBuilder(threadTestContext, webElement);
        actionResult.setStepStatus(Status.PASS);
        Media media = reportAction(context, element, description, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, "WaitForVisibleAndClickable");
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }

    public void conditionsBuilder(ThreadTestContext context, WebElement webElement) {
        Wait<WebDriver> wait = new FluentWait<>(context.getDriver())
                .withTimeout(java.time.Duration.ofSeconds(webDriverWaitTimeoutSeconds))
                .pollingEvery(java.time.Duration.ofSeconds(1));
        wait.until(
                ExpectedConditions.and(
                        ExpectedConditions.visibilityOf(webElement),
                        ExpectedConditions.elementToBeClickable(webElement)
                )
        );
    }
}
