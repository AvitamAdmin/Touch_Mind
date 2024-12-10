package com.cheil.qa.service;

import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.framework.ThreadTestContext;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;

import java.util.List;

public interface CrawlerService {

    WebElement getUiElement(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);

    List<WebElement> getUiElements(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);

    WebElement getBy(ThreadTestContext threadTestContext, CrawlerElementAttributes crawlerElementAttributes);
}
