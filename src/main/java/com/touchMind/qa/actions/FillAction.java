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
import java.util.stream.Collectors;

import static com.touchMind.qa.utils.ReportUtils.decrypt;
import static com.touchMind.qa.utils.ReportUtils.reportAction;

@Service(ActionType.INPUT_TEXT)
public class FillAction implements ElementActionService {

    public static final String CONSTANT_VAL = "Constant_val";
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private TestProfileRepository testProfileRepository;
    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator testLocator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        LocatorPriority locatorPriority = actionRequest.getLocatorPriority();
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String itemSite = TestDataUtils.getString(testData, TestDataUtils.Field.SITE_ISOCODE);
        String testProfileId = TestDataUtils.getString(testData, TestDataUtils.Field.TEST_PROFILE);

        String inputValue = null;
        String encryptedInputVal = "";
        if (StringUtils.isNotEmpty(testProfileId)) {
            TestProfile testProfile = testProfileRepository.findByIdentifier(testProfileId);
            if (testProfile != null) {
                List<ProfileLocatorDto> profileLocatorList = testProfile.getProfileLocators();
                if (CollectionUtils.isNotEmpty(profileLocatorList)) {
                    List<ProfileLocatorDto> profileLocatorOptional = profileLocatorList.stream().filter(identifier -> identifier.getLocatorId().equals(testLocator.getIdentifier())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(profileLocatorOptional)) {
                        Optional<ProfileLocatorDto> profileLocator = profileLocatorOptional.stream().findFirst();
                        if (profileLocator.isPresent()) {
                            inputValue = profileLocator.get().getInputValue();
                            encryptedInputVal = inputValue;
                            inputValue = inputValue.contains(CONSTANT_VAL) ? decrypt(inputValue.replaceAll(CONSTANT_VAL, "")) : inputValue;
                        }
                    }
                }
            }
        }
        ActionResult actionResult = new ActionResult();
        actionResult.setStepStatus(Status.FAIL);
        WebElement element = selectorService.getUiElement(context, testLocator);
        String description = null;
        if (testLocator != null) {
            description = testLocator.getShortDescription() + ", Selector info : " + testLocator.getUiLocatorSelectorToString(itemSite);
        }
        if (element == null) {
            actionResult.setMessage("Failed to find locator in the page - " + testLocator.getIdentifier() + ". Please check locator configuration: " + description);
            return actionResult;
        }

        element.clear();
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        if (StringUtils.isEmpty(inputValue)) {
            inputValue = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
        }

        if (BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
            element.sendKeys(threadTestContext.getData().get(inputValue));
        } else {
            element.sendKeys(inputValue);
        }
        Media media = reportAction(context, element, description, testLocator.getIdentifier() + ", Input data:  " + (StringUtils.isNotEmpty(encryptedInputVal) ? encryptedInputVal : inputValue), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getShortDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.INPUT_TEXT);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        actionResult.setStepStatus(Status.PASS);
        return actionResult;
    }
}
