package com.touchmind.qa.actions;

import com.touchmind.core.mongo.model.ShopNavigation;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.ShopNavigationRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.strategies.CrawlerFactory;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
import com.touchmind.qa.utils.TestDataUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.Optional;

@Service(ActionType.CRAWLER_ACTION)
public class TreeAction implements ElementActionService {
    @Autowired
    private CrawlerFactory crawlerFactory;
    @Autowired
    private ShopNavigationRepository shopNavigationRepository;

    private final Logger LOG = LoggerFactory.getLogger(TreeAction.class);

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator locator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        JSONObject testDataJson = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testDataJson, TestDataUtils.Field.IS_DEBUG));
        String shopCampaign = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SHOP_CAMPAIGN);
        ShopNavigation shopNavigation = getNavigationTree(threadTestContext, testDataJson, shopCampaign);
        ActionResult actionResult = new ActionResult();
        if (StringUtils.isEmpty(shopCampaign)) {
            LOG.error("Missing the campaign configuration in cronjob, make sure campaign is assigned unable to create the tree!");
            //actionResult.setActionResult(locator.getIdentifier(), locator.getDescription() + " : Missing the campaign configuration in cronjob, make sure campaign is assigned unable to create the tree!", null, Status.FAIL);
            return actionResult;
        }
        WebElement element = null;

        if (shopNavigation == null) {
            //actionResult.setActionResult(locator.getIdentifier(), locator.getDescription() + " : missing the dom tree! aborting the process!", null, Status.FAIL);
            return actionResult;
        }
        if (isDebug) {
            reportAction(context, element, shopNavigation != null && MapUtils.isNotEmpty(shopNavigation.getNavigationTree()) ? shopNavigation.getNavigationTree().toString() : "", locator.getIdentifier(), false);
        }

        String itemSite = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = locator.getUiLocatorSelector(itemSite);
        actionResult = crawlerFactory.performAction(context, shopNavigation, getSelectorType(locatorSelectorDto), locatorGroupData.isTakeAScreenshot());
        reportAction(context, element, locator.getDescription(), locator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        return actionResult;
    }

    private ShopNavigation getNavigationTree(ThreadTestContext threadTestContext, JSONObject testDataJson, String shopCampaign) {
        String sku = testDataJson.getString(TestDataUtils.Field.SKU.toString());
        String itemSite = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
        String subsidiary = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SUBSIDIARY);
        List<ShopNavigation> shopNavigationList = shopNavigationRepository.findBySubsidiaryAndSiteAndVariantAndShopCampaignOrderByCreationTimeDesc(subsidiary, itemSite, sku, shopCampaign);
        Optional<ShopNavigation> shopNavigationOptional = shopNavigationList.stream().findFirst();
        return shopNavigationOptional.orElse(null);
    }

    private String getSelectorType(LocatorSelectorDto locatorSelectorDto) {
        if (StringUtils.isNotEmpty(locatorSelectorDto.getCssSelector())) {
            return "css";
        }

        if (StringUtils.isNotEmpty(locatorSelectorDto.getXpathSelector())) {
            return "xpath";
        }

        if (StringUtils.isNotEmpty(locatorSelectorDto.getIdSelector())) {
            return "id";
        }
        return "other";
    }
}
