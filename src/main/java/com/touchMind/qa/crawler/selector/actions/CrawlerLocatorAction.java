package com.touchMind.qa.crawler.selector.actions;

import com.touchMind.qa.crawler.selector.CrawlerElementAttributes;
import com.touchMind.qa.service.ActionResult;
import org.testng.ITestContext;

public interface CrawlerLocatorAction {
    ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);
}
