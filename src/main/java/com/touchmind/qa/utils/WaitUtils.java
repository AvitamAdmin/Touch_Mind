package com.touchmind.qa.utils;

import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.touchmind.qa.utils.ReportUtils.ACTION_DESCRIPTION;

public class WaitUtils {
    public static final String EXPECTED_TEXT_IS = "expected.text.is";
    public static final String COLON_SPACE_QUOTES = ": \"";
    public static final String QUOTES_DOT_SPACE = "\". ";
    public static final String WAITING_FOR_TEXT_TO_BE_VISIBLE_ON_PAGE = "waiting.for.text.to.be.visible.on.page";
    public static final String EXPECTED_TEXT_XPATH = "//body//*[contains(text(), '%s')]";
    public static final String WAITING_FOR_THE_FOLLOWING_ELEMENTS = "waiting.for.the.following.elements";
    public static final String DELIMITER = ", ";

    public static void waitForTextToBeVisible(ITestContext context, String expectedText) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        ReportUtils.info(context,
                BeanUtils.getLocalizedString(ACTION_DESCRIPTION) +
                        COLON_SPACE_QUOTES +
                        BeanUtils.getLocalizedString(WAITING_FOR_TEXT_TO_BE_VISIBLE_ON_PAGE) +
                        QUOTES_DOT_SPACE +
                        BeanUtils.getLocalizedString(EXPECTED_TEXT_IS) +
                        COLON_SPACE_QUOTES +
                        expectedText +
                        QUOTES_DOT_SPACE, StringUtils.EMPTY,
                false);
        waitForElements(context,
                locator -> List.of(
                        ExpectedConditions.textToBePresentInElementLocated(locator, expectedText)
                ),
                By.xpath(String.format(EXPECTED_TEXT_XPATH, expectedText)));
    }

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
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());

        try {
            ReportUtils.info(context,
                    BeanUtils.getLocalizedString(WAITING_FOR_THE_FOLLOWING_ELEMENTS) + convertToString(locators), StringUtils.EMPTY,
                    false);

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

    public static void waitForElementToVisible(ThreadTestContext context, WebElement webElement, int webDriverWaitTimeoutSeconds) {
        Wait<WebDriver> wait = new FluentWait<>(context.getDriver())
                .withTimeout(java.time.Duration.ofSeconds(webDriverWaitTimeoutSeconds))
                .pollingEvery(java.time.Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOf(webElement));
    }

    public static void wait(ThreadTestContext context, int webDriverWaitTimeoutSeconds) {
        context.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(webDriverWaitTimeoutSeconds));
    }

    public static WebElement waitForElementToLoad(ThreadTestContext context, By by, int waitTimeInSeconds) {

        Wait<WebDriver> wait = new FluentWait<>(context.getDriver())
                .withTimeout(Duration.ofSeconds(waitTimeInSeconds))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);
        WebElement clickseleniumlink = wait.until(ExpectedConditions.elementToBeClickable(by));
        /*
        WebElement clickseleniumlink = wait.until(new Function<WebDriver, WebElement>(){

            public WebElement apply(WebDriver driver ) {
                return driver.findElement(by);
            }
        });

         */
        return clickseleniumlink;
    }

    public static List<WebElement> waitForElementsToLoad(ThreadTestContext context, By by, int waitTimeInSeconds) {
        Wait<WebDriver> wait = new FluentWait<>(context.getDriver())
                .withTimeout(Duration.ofSeconds(waitTimeInSeconds))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(NoSuchElementException.class);
        return wait.until(driver -> driver.findElements(by));
    }

}
