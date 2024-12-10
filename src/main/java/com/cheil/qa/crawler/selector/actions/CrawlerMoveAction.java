package com.cheil.qa.crawler.selector.actions;

import com.aventstack.extentreports.model.Media;
import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.CrawlerService;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service(CrawlerLocatorType.CRAWLER_MOVE_TO)
public class CrawlerMoveAction implements CrawlerLocatorAction {

    Logger LOG = LoggerFactory.getLogger(CrawlerMoveAction.class);

    @Value("${testng.webdriver.explicit.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;

    @Autowired
    private CrawlerService crawlerService;

    @Override
    public ActionResult performAction(ITestContext context, CrawlerElementAttributes crawlerElementAttributes) {

        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        ActionResult actionResult = new ActionResult();
        String tabId = getWindowNameForTabNumber(threadTestContext, crawlerElementAttributes);
        if (StringUtils.isEmpty(tabId)) {
            //actionResult.setActionResult(crawlerElementAttributes.getLocator() + ":" + crawlerElementAttributes.getMethodType(), "Invalid tab id", null, Status.FAIL, crawlerElementAttributes.getComponent(), "CrawlerMoveAction: invalid tab id could not switch to tab!");
        }
        try {
            Thread.sleep(webDriverWaitTimeoutSeconds * 1000L);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        WebElement webElement = null;
        Media media = reportAction(context, webElement, crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), false);
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.INFO, crawlerElementAttributes.getComponent(), "CrawlerMoveAction: Switch to tab action started!");
        threadTestContext.getDriver().switchTo().window(tabId);
        //actionResult.setActionResult(crawlerElementAttributes.getLocator(), crawlerElementAttributes.getMethodType(), media != null ? media.getPath() : null, Status.PASS, crawlerElementAttributes.getComponent(), "CrawlerMoveAction: Switch to tab success!");
        return actionResult;
    }

    private String getWindowNameForTabNumber(ThreadTestContext threadTestContext, CrawlerElementAttributes crawlerElementAttributes) {
        Set<String> handles = threadTestContext.getDriver().getWindowHandles();
        String tabNumberStr = crawlerElementAttributes.getLocator();

        try {
            int tabNumber = Integer.valueOf(tabNumberStr.substring(tabNumberStr.indexOf("[") + 1, tabNumberStr.indexOf("]")));
            if (handles.size() < tabNumber) {
                return null;
            }
            AtomicInteger counter = new AtomicInteger();
            AtomicReference<String> handle = new AtomicReference<>();
            handles.stream().forEach(h -> {
                counter.getAndIncrement();
                if (counter.get() == tabNumber) {
                    handle.set(h);
                }
            });
            return handle.get();
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
}
