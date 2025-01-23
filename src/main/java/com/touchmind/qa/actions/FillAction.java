package com.touchmind.qa.actions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.dto.ProfileLocatorDto;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.QaLocatorResultReport;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.model.TestProfile;
import com.touchmind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchmind.core.mongo.repository.TestProfileRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.utils.TestDataUtils;
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

import static com.touchmind.qa.utils.ReportUtils.decrypt;
import static com.touchmind.qa.utils.ReportUtils.reportAction;

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
            TestProfile testProfile = testProfileRepository.findByRecordId(testProfileId);
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
        WebElement element = selectorService.getUiElement(context, testLocator);
        if (element == null) {
            Media media = reportAction(context, element, testLocator.getDescription() + " Data:" + encryptedInputVal, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
            ActionResult actionResult = new ActionResult();
            actionResult.setStepStatus(Status.FAIL);
            return actionResult;
        }

        element.clear();
        ActionResult actionResult = new ActionResult();
        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
        String inputData = locatorSelectorDto != null ? locatorSelectorDto.getInputData() : "";
        //TODO check if encryption is correctly working
        if (StringUtils.isNotEmpty(inputValue)) {
            actionResult.setStepStatus(Status.PASS);
            element.sendKeys(inputValue);
        } else if (BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
            actionResult.setStepStatus(Status.PASS);
            element.sendKeys(threadTestContext.getData().get(inputData));
        } else {
            actionResult.setStepStatus(Status.PASS);
            element.sendKeys(inputData);
        }
        Media media = reportAction(context, element, testLocator.getDescription(), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
        QaLocatorResultReport qaLocatorResultReport = new QaLocatorResultReport();
        qaLocatorResultReport = qaLocatorResultReport.getQaLocatorResultReport(actionRequest.getQaTestResultId(), testLocator.getIdentifier(), testLocator.getDescription(), media != null ? media.getPath() : null, Status.INFO, null, ActionType.INPUT_TEXT);
        qaLocatorResultReportRepository.save(qaLocatorResultReport);
        return actionResult;
    }
}
