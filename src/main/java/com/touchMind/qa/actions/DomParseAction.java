package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.touchMind.core.mongo.model.Campaign;
import com.touchMind.core.mongo.model.NavigationLink;
import com.touchMind.core.mongo.model.ShopNavigation;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.repository.CampaignRepository;
import com.touchMind.core.mongo.repository.ShopNavigationRepository;
import com.touchMind.core.service.CoreService;
import com.touchMind.form.LocatorGroupData;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.framework.ThreadTestContext;
import com.touchMind.qa.service.ActionRequest;
import com.touchMind.qa.service.ActionResult;
import com.touchMind.qa.service.ElementActionService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionType;
import com.touchMind.qa.utils.TestDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.touchMind.qa.utils.ReportUtils.reportAction;
import static com.touchMind.qa.utils.ReportUtils.savePageSource;

@Service(ActionType.DOM_PARSE_ACTION)
public class DomParseAction implements ElementActionService {
    public static final String COLON = ":";
    private final Logger LOG = LoggerFactory.getLogger(DomParseAction.class);
    @Autowired
    Environment env;
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private CoreService coreService;
    @Autowired
    private ShopNavigationRepository shopNavigationRepository;
    @Autowired
    private CampaignRepository campaignRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator locator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        JSONObject testDataJson = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        boolean isDebug = BooleanUtils.toBoolean(TestDataUtils.getString(testDataJson, TestDataUtils.Field.IS_DEBUG));
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        try {
            List<WebElement> elements = selectorService.getUiElements(context, locator);
            if (CollectionUtils.isEmpty(elements)) {
                return actionResult;
            }
            reportAction(context, elements, locator.getShortDescription(), locator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
            ShopNavigation shopNavigation = getShopNavigation(elements, context, locator);
            if (shopNavigation == null) {
                if (isDebug) {
                    reportAction(context, Collections.emptyList(), locator.getShortDescription() + " " + savePageSource(context, env.getProperty("server.url")), locator.getIdentifier(), false);
                }
                return actionResult;
            }
            shopNavigationRepository.save(shopNavigation);
        } catch (Exception e) {
            if (isDebug) {
                reportAction(context, Collections.emptyList(), locator.getShortDescription() + " " + savePageSource(context, env.getProperty("server.url")), locator.getIdentifier(), false);
            }
            throw e;
        }
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }

    private ShopNavigation getShopNavigation(List<WebElement> elements, ITestContext context, TestLocator locator) {
        JSONObject testDataJson = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String shopCampaign = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SHOP_CAMPAIGN);
        if (StringUtils.isEmpty(shopCampaign)) {
            LOG.error("Missing the campaign configuration in cronjob, make sure campaign is assigned unable to create the tree!");
            return null;
        }
        String itemSite = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SITE_ISOCODE);
        String subsidiary = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SUBSIDIARY);
        Campaign campaign = campaignRepository.findByStatusAndIdentifierOrderByIdentifier(true, shopCampaign);
        if (campaign == null) {
            LOG.error("Missing the campaign unable to create the tree!" + shopCampaign);
            return null;
        }
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        String sku = TestDataUtils.getString(testDataJson, TestDataUtils.Field.SKU, threadTestContext.getTestIdentifier());
        String node = TestDataUtils.getString(testDataJson, TestDataUtils.Field.NODE);
        String identifier = itemSite + subsidiary + sku + node;
        ShopNavigation shopNavigation = shopNavigationRepository.findByIdentifier(identifier);

        if (shopNavigation == null) {
            shopNavigation = new ShopNavigation();
        }
        shopNavigation.setCreationTime(new Date());
        shopNavigation.setLastModified(new Date());
        shopNavigation.setStatus(true);
        shopNavigation.setSite(itemSite);
        shopNavigation.setSubsidiary(subsidiary);
        shopNavigation.setNode(node);
        shopNavigation.setVariant(sku);
        shopNavigation.setIdentifier(identifier);
        shopNavigation.setCreator(testDataJson.getString("currentUser"));
        shopNavigation.setShopCampaign(shopCampaign);
        LocatorSelectorDto locatorSelectorDto = locator.getUiLocatorSelector(itemSite);
        String attributeName = locatorSelectorDto.getXpathSelector();
        if (StringUtils.isEmpty(attributeName)) {
            attributeName = locatorSelectorDto.getCssSelector();
        } else if (StringUtils.isEmpty(attributeName)) {
            attributeName = locatorSelectorDto.getIdSelector();
        } else if (StringUtils.isEmpty(attributeName)) {
            attributeName = locatorSelectorDto.getIdSelector();
        } else if (StringUtils.isEmpty(attributeName)) {
            attributeName = locatorSelectorDto.getOthersSelector();
        }
        Map<String, List<NavigationLink>> navigationTree = constructTree(context, elements, attributeName, campaign);
        shopNavigation.setNavigationTree(navigationTree);
        shopNavigation.setActiveComponents(getActiveComponents(navigationTree));
        return shopNavigation;
    }

    private List<String> getActiveComponents(Map<String, List<NavigationLink>> navigationTree) {
        List<String> componentList = new ArrayList<>();
        navigationTree.keySet().forEach(component -> {
            List<NavigationLink> links = navigationTree.get(component);
            if (links.stream().anyMatch(NavigationLink::isUsed)) {
                componentList.add(component);
            }
        });
        return componentList;
    }

    private Map<String, List<NavigationLink>> constructTree(ITestContext context, List<WebElement> elements, String attribute, Campaign campaign) {
        Map<String, List<NavigationLink>> navigationTree = new LinkedHashMap<>();
        elements.forEach(element -> {
            try {
                String attributeValue = null;
                if (attribute.contains("[") || attribute.contains("]")) {
                    attributeValue = element.getAttribute(attribute.substring(attribute.indexOf("[") + 1, attribute.indexOf("]")));
                } else {
                    attributeValue = element.getAttribute(attribute);
                }
                if (StringUtils.isNotEmpty(attributeValue)) {
                    String[] properties = attributeValue.split(COLON);
                    String selectorKey = properties[properties.length - 2];
                    String selectorValue = "[" + attribute.substring(attribute.indexOf("[") + 1, attribute.indexOf("]")) + "='" + attributeValue + "']";
                    NavigationLink navigationLink = new NavigationLink();
                    navigationLink.setSelector(selectorValue);
                    navigationLink.setUsed(evaluateLink(campaign.getDomPaths(), attributeValue));
                    if (navigationTree.containsKey(selectorKey)) {
                        List<NavigationLink> children = navigationTree.get(properties[properties.length - 2]);
                        children.add(navigationLink);
                    } else {
                        List<NavigationLink> navigationLinks = new ArrayList<>();
                        navigationLinks.add(navigationLink);
                        navigationTree.put(selectorKey, navigationLinks);
                    }
                }
            } catch (Exception e) {
                reportAction(context, Collections.emptyList(), element.toString() + " " + savePageSource(context, env.getProperty("server.url")), element.toString(), false);
            }
        });
        return navigationTree;
    }

    private boolean evaluateLink(List<String> domPaths, String link) {
        return domPaths.stream().anyMatch(link::contains);
    }
}
