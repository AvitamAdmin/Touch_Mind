package com.touchmind.qa.crawler.selector.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.qa.crawler.selector.CrawlerElementAttributes;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.CrawlerService;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import com.touchmind.qa.utils.TestDataUtils;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(CrawlerLocatorType.CRAWLER_BACK)
public class CrawlerBackAction implements CrawlerLocatorAction {

    Logger LOG = LoggerFactory.getLogger(CrawlerBackAction.class);
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
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.INFO, crawlerElementAttributes.getComponent(), "CrawlerBackAction: Back action started!");
        threadTestContext.getDriver().navigate().back();
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.PASS, crawlerElementAttributes.getComponent(), "CrawlerBackAction: Back action Success!");
        return actionResult;
    }
}
