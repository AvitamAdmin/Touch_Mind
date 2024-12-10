package com.touchmind.qa.crawler.selector.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.qa.crawler.selector.CrawlerElementAttributes;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.CrawlerService;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import com.touchmind.qa.utils.ScrollUtils;
import com.touchmind.qa.utils.TestDataUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(CrawlerLocatorType.CRAWLER_CLICK_ACTION)
public class CrawlerClickAction implements CrawlerLocatorAction {

    public static final String JAVASCRIPT_FORCIBLY_CLICK = "arguments[0].click();";

    @Autowired
    private CrawlerService crawlerService;

    @Override
    public ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes) {

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        WebElement element = crawlerService.getUiElement(context, crawlerElementAttributes);
        ActionResult actionResult = new ActionResult();
        if (element == null || !element.isDisplayed()) {
            //actionResult.setActionResult(crawlerElementAttributes.getLocator(), "CrawlerClickAction: Element is either null or not visible", null, Status.FAIL, crawlerElementAttributes.getComponent(), "CrawlerClickAction: Element is either null or not visible");
            return actionResult;
        }
        Media media = reportAction(context, element, crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), crawlerElementAttributes.isTakeAScreenshot());
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.INFO, crawlerElementAttributes.getComponent(), "CrawlerClickAction: action initiated!");
        ScrollUtils.scrollIntoView(threadTestContext, element);
        ((JavascriptExecutor) threadTestContext.getDriver()).executeScript(JAVASCRIPT_FORCIBLY_CLICK, element);
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.PASS, crawlerElementAttributes.getComponent(), "CrawlerClickAction: action success!");
        return actionResult;
    }
}
