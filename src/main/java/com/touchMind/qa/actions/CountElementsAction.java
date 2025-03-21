package com.touchMind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchMind.core.mongo.dto.ProfileLocatorDto;
import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.core.mongo.model.TestProfile;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.core.mongo.repository.TestProfileRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

import java.util.List;
import java.util.Optional;

import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.CHECK_IF_ELEMENT_PRESENT)
public class CountElementsAction implements ElementActionService {

    @Autowired
    private SelectorService selectorService;

    @Autowired
    private TestProfileRepository testProfileRepository;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    public ActionResult performAction(ActionRequest actionRequest) {

        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        List<WebElement> element = selectorService.getUiElements(context, testLocator);
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        String description = null;
        if (testLocator != null) {
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        if (CollectionUtils.isEmpty(element)) {
            actionResult.setMessage("Failed to find locator in the page - " + testLocator.getIdentifier() + ". Please check locator configuration: " + description);
            return actionResult;
        }

        String testProfileId = TestDataUtils.getString(testData, TestDataUtils.Field.TEST_PROFILE);

        String inputValue = null;
        if (StringUtils.isNotEmpty(testProfileId)) {
            TestProfile testProfile = testProfileRepository.findByIdentifier(testProfileId);
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
            String value = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
            if (StringUtils.isNotEmpty(inputValue)) {
                if (Long.valueOf(inputValue) >= element.size()) {
                    actionResult.setStepStatus(Status.PASS);
                }
            } else if (StringUtils.isNotEmpty(value) && BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
                if (Long.valueOf(threadTestContext.getData().get(value)) >= element.size()) {
                    actionResult.setStepStatus(Status.PASS);
                }
            } else if (StringUtils.isNotEmpty(value)) {
                if (Long.valueOf(value) >= element.size()) {
                    actionResult.setStepStatus(Status.PASS);
                }
            } else {
                if (element.size() > 0) {
                    actionResult.setStepStatus(Status.PASS);
                }
            }
        }
        Media media = reportAction(context, element, description, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.CHECK_IF_ELEMENT_PRESENT);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }

}
