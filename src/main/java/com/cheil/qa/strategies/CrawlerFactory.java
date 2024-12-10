package com.cheil.qa.strategies;

import com.aventstack.extentreports.Status;
import com.cheil.core.mongo.model.NavigationLink;
import com.cheil.core.mongo.model.ShopNavigation;
import com.cheil.qa.service.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;

import java.util.List;
import java.util.Map;

@Component
public class CrawlerFactory {
    Logger LOG = LoggerFactory.getLogger(CrawlerFactory.class);
    @Autowired
    private CrawlerLocatorFactory crawlerLocatorFactory;

    public ActionResult performAction(ITestContext context, ShopNavigation shopNavigation, String type, boolean isTakeAScreenshot) {
        ActionResult actionResult = new ActionResult();
        boolean isSuccess = true;
        Map<String, List<NavigationLink>> shopNavigationTree = shopNavigation.getNavigationTree();
        List<String> activeComponents = shopNavigation.getActiveComponents();
        for (String parentNode : shopNavigationTree.keySet()) {
            if (activeComponents.contains(parentNode)) {
                List<NavigationLink> children = shopNavigationTree.get(parentNode);
                for (NavigationLink child : children) {
                    if (child.isUsed()) {
                        try {
                            ActionResult result = crawlerLocatorFactory.performPreAction(context, child, type, isTakeAScreenshot);
                            if (result != null) {
                                //actionResult.getQaLocatorResultReports().addAll(result.getQaLocatorResultReports());
                                if (result.getStepStatus() != null && result.getStepStatus().equals(Status.FAIL)) {
                                    isSuccess = false;
                                }
                            }
                        } catch (Exception e) {
                            LOG.error(e.getMessage());
                        }
                    }
                }
            }
        }
        if (!isSuccess) {
            //actionResult.setActionResult(type, "Test Failed", null, Status.FAIL, shopNavigation.getIdentifier(), "Failed to process the test!");
        }
        //if (CollectionUtils.isEmpty(actionResult.getQaLocatorResultReports())) {
            //actionResult.setActionResult(type, "There was no children found for processing!", null, Status.PASS, shopNavigation.getIdentifier(), "Success!");
        //}
        return actionResult;
    }
}
