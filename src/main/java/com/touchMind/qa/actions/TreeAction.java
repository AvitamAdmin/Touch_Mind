package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchMind.core.mongo.model.ShopNavigation;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.ShopNavigationRepository;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.strategies.CrawlerFactory;
import com.touchMind.qa.utils.TestDataUtils;
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

import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.CRAWLER_ACTION)
public class TreeAction implements ElementActionService {
    private final Logger LOG = LoggerFactory.getLogger(TreeAction.class);
    @Autowired
    private CrawlerFactory crawlerFactory;
    @Autowired
    private ShopNavigationRepository shopNavigationRepository;

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
        actionResult.setStepStatus(Status.FAIL);
        if (StringUtils.isEmpty(shopCampaign)) {
            String msg = "Missing the campaign configuration in cronjob, make sure campaign is assigned unable to create the tree!";
            actionResult.setMessage(msg);
            LOG.error(msg);
            return actionResult;
        }
        WebElement element = null;

        if (shopNavigation == null) {
            actionResult.setMessage("Shop Navigation is null. Please check navigation tree data");
            return actionResult;
        }
        if (isDebug) {
            reportAction(context, element, shopNavigation != null && MapUtils.isNotEmpty(shopNavigation.getNavigationTree()) ? shopNavigation.getNavigationTree().toString() : "", locator.getIdentifier(), false);
        }

        String itemSite = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
        LocatorSelectorDto locatorSelectorDto = locator.getUiLocatorSelector(itemSite);
        actionResult = crawlerFactory.performAction(context, shopNavigation, getSelectorType(locatorSelectorDto), locatorGroupData.isTakeAScreenshot());
        reportAction(context, element, locator.getShortDescription(), locator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        actionResult.setStepStatus(Status.PASS);
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
