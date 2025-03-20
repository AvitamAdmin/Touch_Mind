package com.touchMind.qa.service;

import com.touchMind.qa.crawler.selector.CrawlerElementAttributes;
import com.touchMind.qa.framework.ThreadTestContext;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;

import java.util.List;

public interface CrawlerService {

    WebElement getUiElement(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);

    List<WebElement> getUiElements(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);

    WebElement getBy(ThreadTestContext threadTestContext, CrawlerElementAttributes crawlerElementAttributes);
}
