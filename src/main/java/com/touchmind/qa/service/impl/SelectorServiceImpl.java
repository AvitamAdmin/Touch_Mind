package com.touchmind.qa.service.impl;

import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.utils.ReportUtils;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;

@Service
public class SelectorServiceImpl implements SelectorService {
    public static final String N_A = "N/A";
    public static final String UI_ELEMENT_RETRIEVAL_ID_S_METHOD_S_RESULTING_SELECTOR_S = "ui.element.retrieval.id.method.result";
    public static final String CSS_SELECTOR_STRATEGY = "cssSelector";
    public static final String XPATH_SELECTOR_STRATEGY = "xPath";
    public static final String ID_SELECTOR_STRATEGY = "id";
    public static final List<String> SELECTOR_STRATEGIES = List.of(CSS_SELECTOR_STRATEGY, XPATH_SELECTOR_STRATEGY, ID_SELECTOR_STRATEGY);
    Logger LOG = LoggerFactory.getLogger(SelectorServiceImpl.class);

    @Override
    public WebElement getUiElement(ITestContext context, TestLocator locator) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        logUiElementRetrievalResult(context, locator.getMethodName(), String.valueOf(locator.getUiLocatorSelector(itemSite)));
        return getBy(threadTestContext, locator.getUiLocatorSelector(itemSite));
    }

    @Override
    public List<WebElement> getUiElements(ITestContext context, TestLocator locator) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        logUiElementRetrievalResult(context, locator.getMethodName(), locator.getUiLocatorSelector(itemSite).toString());
        return getBys(threadTestContext, locator.getUiLocatorSelector(itemSite));
    }

    @Override
    public WebElement getBy(ThreadTestContext threadTestContext, LocatorSelectorDto locatorSelectorDto) {
        WebElement element = null;
        if (StringUtils.isNotEmpty(locatorSelectorDto.getXpathSelector())) {
            By by = By.xpath(locatorSelectorDto.getXpathSelector());
            element = getWebElement(threadTestContext, by);
        }
        if (element == null) {
            if (StringUtils.isNotEmpty(locatorSelectorDto.getCssSelector())) {
                By by = By.cssSelector(locatorSelectorDto.getCssSelector());
                element = getWebElement(threadTestContext, by);
            }
        }
        if (element == null) {
            if (StringUtils.isNotEmpty(locatorSelectorDto.getIdSelector())) {
                By by = By.id(locatorSelectorDto.getIdSelector());
                element = getWebElement(threadTestContext, by);
            }
        }
        return element;
    }

    private List<WebElement> getWebElements(ThreadTestContext threadTestContext, By by) {
        List<WebElement> webElement = null;
        try {
            webElement = threadTestContext.getDriver().findElements(by);
        } catch (Exception e) {
            LOG.error("Web element selection for Locator " + by.toString() + " failed!");
        }
        return webElement;
    }

    private WebElement getWebElement(ThreadTestContext threadTestContext, By by) {
        WebElement webElement = null;
        try {
            webElement = threadTestContext.getDriver().findElement(by);
        } catch (Exception e) {
            LOG.error("Web element selection for Locator " + by.toString() + " failed!");
        }
        return webElement;
    }

    public List<WebElement> getBys(ThreadTestContext threadTestContext, LocatorSelectorDto locatorSelectorDto) {
        if (StringUtils.isNotEmpty(locatorSelectorDto.getXpathSelector())) {
            By by = By.xpath(locatorSelectorDto.getXpathSelector());
            return getWebElements(threadTestContext, by);
        }
        if (StringUtils.isNotEmpty(locatorSelectorDto.getCssSelector())) {
            By by = By.xpath(locatorSelectorDto.getCssSelector());
            return getWebElements(threadTestContext, by);
        }
        return null;
    }

    private void logUiElementRetrievalResult(ITestContext context,
                                             String methodName,
                                             String selectorStr) {
        String logMessage = String.format(BeanUtils.getLocalizedString(UI_ELEMENT_RETRIEVAL_ID_S_METHOD_S_RESULTING_SELECTOR_S),
                methodName,
                selectorStr);
        ReportUtils.info(context, logMessage, StringUtils.EMPTY, false);
    }
}
