package com.cheil.qa.service.impl;

import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.CrawlerService;
import com.cheil.qa.utils.ReportUtils;
import com.cheil.qa.utils.TestDataUtils;
import com.cheil.qa.utils.WaitUtils;
import com.cheil.utils.BeanUtils;
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
public class CrawlerServiceImpl implements CrawlerService {

    public static final String UI_ELEMENT_RETRIEVAL_ID_S_METHOD_S_RESULTING_SELECTOR_S = "ui.element.retrieval.id.method.result";
    Logger LOG = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    @Override
    public WebElement getUiElement(ITestContext context, CrawlerElementAttributes crawlerElementAttributes) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        logUiElementRetrievalResult(context, crawlerElementAttributes.getMethodType(), crawlerElementAttributes.getLocator());
        return getBy(threadTestContext, crawlerElementAttributes);
    }

    @Override
    public List<WebElement> getUiElements(ITestContext context, CrawlerElementAttributes crawlerElementAttributes) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        logUiElementRetrievalResult(context, crawlerElementAttributes.getMethodType(), crawlerElementAttributes.getLocator());
        return getBys(threadTestContext, crawlerElementAttributes);
    }

    @Override
    public WebElement getBy(ThreadTestContext threadTestContext, CrawlerElementAttributes crawlerElementAttributes) {

        if (StringUtils.isEmpty(crawlerElementAttributes.getLocator()) || StringUtils.isEmpty(crawlerElementAttributes.getMethodType())) {
            LOG.error("Locator : " + crawlerElementAttributes.getLocator() + " or method type:" + crawlerElementAttributes.getMethodType() + " cannot be empty!");
            return null;
        }
        By by = null;
        if ("css".equals(crawlerElementAttributes.getMethodType())) {
            by = By.cssSelector(crawlerElementAttributes.getLocator());
        }
        if ("xpath".equals(crawlerElementAttributes.getMethodType())) {
            by = By.xpath(crawlerElementAttributes.getLocator());
        }
        if ("id".equals(crawlerElementAttributes.getMethodType())) {
            by = By.id(crawlerElementAttributes.getLocator());
        }
        if (by == null) {
            LOG.error("Undefined method type: " + crawlerElementAttributes.getMethodType() + " should be css or xpath or id");
            return null;
        }
        return StringUtils.isNotEmpty(crawlerElementAttributes.getWaitTime()) ? WaitUtils.waitForElementToLoad(threadTestContext, by, Integer.valueOf(crawlerElementAttributes.getWaitTime())) : getWebElement(threadTestContext, by);
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

    public List<WebElement> getBys(ThreadTestContext threadTestContext, CrawlerElementAttributes crawlerElementAttributes) {
        if (StringUtils.isEmpty(crawlerElementAttributes.getLocator()) || StringUtils.isEmpty(crawlerElementAttributes.getMethodType())) {
            LOG.error("Locator : " + crawlerElementAttributes.getLocator() + " or method type:" + crawlerElementAttributes.getMethodType() + " cannot be empty!");
            return null;
        }
        By by = null;
        if ("css".equals(crawlerElementAttributes.getMethodType())) {
            by = By.cssSelector(crawlerElementAttributes.getLocator());
        }
        if ("xpath".equals(crawlerElementAttributes.getMethodType())) {
            by = By.xpath(crawlerElementAttributes.getLocator());
        }
        if (by == null) {
            LOG.error("Undefined method type: " + crawlerElementAttributes.getMethodType() + " should be css or xpath or id");
            return null;
        }
        return StringUtils.isNotEmpty(crawlerElementAttributes.getWaitTime()) ? WaitUtils.waitForElementsToLoad(threadTestContext, by, Integer.valueOf(crawlerElementAttributes.getWaitTime())) : getWebElements(threadTestContext, by);
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
