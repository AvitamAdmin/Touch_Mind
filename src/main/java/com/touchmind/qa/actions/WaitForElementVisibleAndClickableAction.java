package com.touchmind.qa.actions;

import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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

@Service(ActionType.WAIT_FOR_VISIBLE_AND_CLICKABLE)
public class WaitForElementVisibleAndClickableAction implements ElementActionService {
    public static final String WAITING_FOR_THE_FOLLOWING_ELEMENTS = "waiting.for.the.following.elements";
    public static final String DELIMITER = ", ";
    @Value("${testng.webdriver.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    @Autowired
    private SelectorService selectorService;

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

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement webElement = selectorService.getUiElement(context, testLocator);
        if (webElement == null) {
            threadTestContext.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(webDriverWaitTimeoutSeconds));
            webElement = selectorService.getUiElement(context, testLocator);
        }
        ActionResult actionResult = new ActionResult();
        if (BooleanUtils.isTrue(locatorPriority.getCheckIfElementPresentOnThePage()) && null == webElement) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }
        conditionsBuilder(threadTestContext, webElement);
        //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.INFO);
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
