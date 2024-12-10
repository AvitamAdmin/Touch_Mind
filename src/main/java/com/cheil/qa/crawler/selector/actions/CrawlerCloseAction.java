package com.cheil.qa.crawler.selector.actions;

import com.aventstack.extentreports.model.Media;
import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.CrawlerService;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(CrawlerLocatorType.CRAWLER_CLOSE_ACTION)
public class CrawlerCloseAction implements CrawlerLocatorAction {

    Logger LOG = LoggerFactory.getLogger(CrawlerCloseAction.class);

    @Value("${testng.webdriver.explicit.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;

    @Autowired
    private CrawlerService crawlerService;

    @Override
    public ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes) {

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        ActionResult actionResult = new ActionResult();
        WebElement element = null;
        try {
            Thread.sleep(webDriverWaitTimeoutSeconds * 1000L);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        Media media = reportAction(context, element, crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), crawlerElementAttributes.isTakeAScreenshot());
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.INFO, crawlerElementAttributes.getComponent(), "CrawlerCloseAction: Close tab Back action started!");
        threadTestContext.getDriver().close();
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.PASS, crawlerElementAttributes.getComponent(), "CrawlerCloseAction: Close tab Back action Success!");
        return actionResult;
    }
}
