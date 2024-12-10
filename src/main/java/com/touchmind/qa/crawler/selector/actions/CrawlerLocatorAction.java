package com.touchmind.qa.crawler.selector.actions;

import com.touchmind.qa.crawler.selector.CrawlerElementAttributes;
import com.touchmind.qa.service.ActionResult;
import org.testng.ITestContext;

public interface CrawlerLocatorAction {
    ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);
}
