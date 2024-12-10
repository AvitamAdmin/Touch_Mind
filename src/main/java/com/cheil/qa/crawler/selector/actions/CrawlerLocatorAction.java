package com.cheil.qa.crawler.selector.actions;

import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.service.ActionResult;
import org.testng.ITestContext;

public interface CrawlerLocatorAction {
    ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes);
}
