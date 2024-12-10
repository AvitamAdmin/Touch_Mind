package com.cheil.qa.strategies;

import com.aventstack.extentreports.model.Media;
import com.cheil.core.mongo.model.CrawlerPath;
import com.cheil.core.mongo.model.NavigationLink;
import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.repository.CrawlerPathRepository;
import com.cheil.core.mongo.repository.SiteRepository;
import com.cheil.qa.crawler.selector.CrawlerElementAttributes;
import com.cheil.qa.crawler.selector.actions.CrawlerLocatorAction;
import com.cheil.qa.crawler.selector.actions.CrawlerLocatorType;
import com.cheil.qa.service.ActionResult;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CrawlerLocatorFactory {

    public static final String CLICK = "click";
    public static final String WAIT = "wait";
    public static final String SKIP = "skip";
    public static final String BACK = "back";
    public static final String MOVE = "move";
    public static final String CLOSE = "close";
    public static final String ACTION_SEPERATOR = "¶";
    public static final String COLON = ":";
    private final Map<String, CrawlerLocatorAction> crawlerLocatorFactoryMap;
    Logger LOG = LoggerFactory.getLogger(CrawlerLocatorFactory.class);
    @Autowired
    private CrawlerPathRepository crawlerPathRepository;

    @Autowired
    private SiteRepository siteRepository;

    public CrawlerLocatorFactory(Map<String, CrawlerLocatorAction> crawlerLocatorFactoryMap) {
        this.crawlerLocatorFactoryMap = crawlerLocatorFactoryMap;
    }

    public CrawlerLocatorAction getAction(String actionType) {
        CrawlerLocatorAction crawlerLocatorAction = crawlerLocatorFactoryMap.get(actionType);
        if (crawlerLocatorAction == null) {
            throw new RuntimeException("Unsupported action type");
        }
        return crawlerLocatorAction;
    }

    public ActionResult performPreAction(ITestContext context, NavigationLink locator, String methodType, boolean isTakeAScreenshot) {
        ActionResult actionResult = new ActionResult();

        if (locator == null || StringUtils.isEmpty(locator.getSelector())) {
            //actionResult.setActionResult(locator + ":" + methodType, "Locator is null!", null, Status.FAIL, locator != null ? locator.getSelector() : null, "CrawlerLocatorFactory: invalid selector definition!");
            return actionResult;
        }
        JSONObject testDataJson = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
        String subsidiary = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SUBSIDIARY);
        List<CrawlerPath> crawlerPaths = crawlerPathRepository.findByPathCategoryAndSites(getKeyForPath(locator), itemSite);
        if (CollectionUtils.isNotEmpty(crawlerPaths)) {
            Site site = siteRepository.findByRecordId(itemSite);
            if (!site.getSubsidiary().equals(subsidiary)) {
                LOG.info("subsidiary doesn't match with site selected - " + subsidiary + " site - " + itemSite);
                crawlerPaths = new ArrayList<>();
            }
        }
        CrawlerPath crawlerPath = getCrawlerPathByPattern(crawlerPaths, locator);
        if (crawlerPath == null) {
            //actionResult.setActionResult(locator + ":" + methodType, "Locator is null!", null, Status.FAIL, locator.getSelector(), "Path definition is missing for locator " + locator + " Hence ignoring further action");
            LOG.info("Path definition is missing for locator " + locator + " Hence ignoring further action");
            return actionResult;
        }
        String component = StringUtils.isNotEmpty(crawlerPath.getPathCategory()) ? crawlerPath.getPathCategory().split(":")[0] : null;
        String[] pathElements = crawlerPath.getCrawlerPath().split("§");
        for (String pathElement : pathElements) {
            CrawlerElementAttributes crawlerElementAttributes = constructCrawlerElement(pathElement, locator, methodType);
            if (crawlerElementAttributes != null) {
                crawlerElementAttributes.setComponent(component);
            }
            try {
                if (!CrawlerLocatorType.CRAWLER_SKIP.equals(crawlerElementAttributes.getCrawlerLocatorType())) {
                    crawlerElementAttributes.setTakeAScreenshot(isTakeAScreenshot);
                    CrawlerLocatorAction crawlerLocatorAction = getAction(crawlerElementAttributes.getCrawlerLocatorType());
                    ActionResult result = crawlerLocatorAction.performAction(context, crawlerElementAttributes);
                    //actionResult.getQaLocatorResultReports().addAll(result.getQaLocatorResultReports());
                }
            } catch (Exception e) {
                WebElement webElement = null;
                Media media = reportAction(context, webElement, locator.getSelector(), pathElement, isTakeAScreenshot);
                //actionResult.setActionResult(locator.getSelector(), pathElement, media != null ? media.getPath() : null, Status.FAIL, crawlerElementAttributes.getLocator(), "CrawlerLocatorFactory: Error processing the selector " + e.getMessage());
                return actionResult;
            }
        }
        return actionResult;
    }

    private CrawlerPath getCrawlerPathByPattern(List<CrawlerPath> crawlerPaths, NavigationLink locator) {
        if (CollectionUtils.isNotEmpty(crawlerPaths)) {
            String selector = locator.getSelector();
            List<CrawlerPath> usedPaths = crawlerPaths.stream().filter(cp -> selector.contains(cp.getPathCategory())).toList();

            if (CollectionUtils.isEmpty(usedPaths)) {
                return null;
            }

            if (usedPaths.size() == 1) {
                return usedPaths.get(0);
            }
            AtomicReference<CrawlerPath> crawlerPath = new AtomicReference<>();
            usedPaths.forEach(cp -> {
                Pattern pattern = Pattern.compile(cp.getPattern());
                Matcher matcher = pattern.matcher(selector);
                if (matcher.matches()) {
                    crawlerPath.set(cp);
                }
            });
            return crawlerPath.get();
        }
        LOG.info("crawlerPaths is empty");
        return null;
    }

    /*
    This method extract component key which is used to get the navigation steps from database.
     */
    private String getKeyForPath(NavigationLink path) {
        if (path == null || StringUtils.isEmpty(path.getSelector())) {
            return null;
        }
        String[] attributes = path.getSelector().split(COLON);
        if (attributes.length < 2) {
            return null;
        }
        String key = attributes[attributes.length - 2] + COLON + attributes[attributes.length - 1].substring(0, attributes[attributes.length - 1].indexOf("'"));
        return key;
    }

    private CrawlerElementAttributes constructCrawlerElement(String element, NavigationLink defaultLocator, String defaultMethodType) {
        if (StringUtils.isEmpty(element)) {
            LOG.error("No valid crawler element defined !");
            return null;
        }

        if (!element.contains("{") && !element.contains("}") && !element.contains(ACTION_SEPERATOR)) {
            LOG.error("invalid crawler element definition");
            return null;
        }

        CrawlerElementAttributes crawlerElementAttributes = new CrawlerElementAttributes();
        String[] elementParts = element.split(ACTION_SEPERATOR);
        String actionPart = elementParts[0];
        String identifierPart = elementParts[1];

        if (elementParts.length < 2 || StringUtils.isEmpty(actionPart) || StringUtils.isEmpty(identifierPart)) {
            LOG.error("invalid crawler element definition should have Action(waitTime):methodType[attribute='cssSelector'");
            return null;
        }
        String waitTime = null;
        String actionType = actionPart;
        if (actionPart.contains("(")) {
            actionType = actionPart.substring(0, actionPart.indexOf("("));
            waitTime = actionPart.substring(actionPart.indexOf("(") + 1, actionPart.indexOf(")"));
        }

        String methodType = identifierPart.substring(0, identifierPart.indexOf("{"));
        String locator = identifierPart.substring(identifierPart.indexOf("{") + 1, identifierPart.indexOf("}"));
        if ("#".equals(locator)) {
            locator = defaultLocator.getSelector();
            methodType = defaultMethodType;
        }
        crawlerElementAttributes.setWaitTime(waitTime);
        crawlerElementAttributes.setLocator(locator);
        crawlerElementAttributes.setMethodType(methodType);
        switch (actionType) {
            case CLICK:
                crawlerElementAttributes.setCrawlerLocatorType(CrawlerLocatorType.CRAWLER_CLICK_ACTION);
                break;
            case SKIP:
                crawlerElementAttributes.setCrawlerLocatorType(CrawlerLocatorType.CRAWLER_SKIP);
                break;
            case BACK:
                crawlerElementAttributes.setCrawlerLocatorType(CrawlerLocatorType.CRAWLER_BACK);
                break;
            case MOVE:
                crawlerElementAttributes.setCrawlerLocatorType(CrawlerLocatorType.CRAWLER_MOVE_TO);
                break;
            case CLOSE:
                crawlerElementAttributes.setCrawlerLocatorType(CrawlerLocatorType.CRAWLER_CLOSE_ACTION);
                break;
        }
        return crawlerElementAttributes;
    }
}
