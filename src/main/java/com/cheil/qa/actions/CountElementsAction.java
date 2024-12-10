package com.cheil.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.cheil.core.mongo.dto.ProfileLocatorDto;
import com.cheil.core.mongo.model.LocatorPriority;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.mongo.model.TestProfile;
import com.cheil.core.mongo.repository.TestProfileRepository;
import com.cheil.form.LocatorGroupData;
import com.cheil.form.LocatorSelectorDto;
import com.cheil.qa.framework.ThreadTestContext;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.service.SelectorService;
import com.cheil.qa.strategies.ActionType;
import static com.cheil.qa.utils.ReportUtils.reportAction;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.Optional;

@Service(ActionType.CHECK_IF_ELEMENT_PRESENT)
public class CountElementsAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;

    @Autowired
    private TestProfileRepository testProfileRepository;

    public ActionResult performAction(ActionRequest actionRequest) {

        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        List<WebElement> element = selectorService.getUiElements(context, testLocator);
        ActionResult actionResult = new ActionResult();
        if (CollectionUtils.isEmpty(element)) {
            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), null, Status.FAIL);
            return actionResult;
        }

        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        String testProfileId = TestDataUtils.getString(testData, TestDataUtils.Field.TEST_PROFILE);

        String inputValue = null;
        if (StringUtils.isNotEmpty(testProfileId)) {
            TestProfile testProfile = testProfileRepository.findByRecordId(testProfileId);
            if (testProfile != null) {
                List<ProfileLocatorDto> profileLocatorList = testProfile.getProfileLocators();
                if (CollectionUtils.isNotEmpty(profileLocatorList)) {
                    Optional<ProfileLocatorDto> profileLocator = profileLocatorList.stream().filter(profileLocatorDto -> profileLocatorDto.getLocatorId().equalsIgnoreCase(testLocator.getIdentifier())).findFirst();
                    if (profileLocator.isPresent()) {
                        inputValue = profileLocator.get().getInputValue();
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(element)) {
            LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
            String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
            Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
            if (StringUtils.isNotEmpty(inputValue)) {
                if (Long.valueOf(inputValue) >= element.size()) {
                    //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
                }
            } else if (StringUtils.isNotEmpty(value) && BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
                if (Long.valueOf(threadTestContext.getData().get(value)) >= element.size()) {
                    //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
                }
            } else if (StringUtils.isNotEmpty(value)) {
                if (Long.valueOf(value) >= element.size()) {
                    //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
                }
            } else {
                if (element.size() > 0) {
                    //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.PASS);
                }
            }
        }
        return actionResult;
    }

}
