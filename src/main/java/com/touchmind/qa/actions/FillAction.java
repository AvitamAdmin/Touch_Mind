package com.touchmind.qa.actions;

import com.aventstack.extentreports.model.Media;
import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.model.TestProfile;
import com.touchmind.core.mongo.repository.TestProfileRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionType;
import static com.touchmind.qa.utils.ReportUtils.decrypt;
import static com.touchmind.qa.utils.ReportUtils.reportAction;
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

@Service(ActionType.INPUT_TEXT)
public class FillAction implements ElementActionService {

    public static final String CONSTANT_VAL = "Constant_val";
    @Autowired
    private SelectorService selectorService;
    @Autowired
    private TestProfileRepository testProfileRepository;

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

//        String inputValue = null;
//        String encryptedInputVal = "";
//        if (StringUtils.isNotEmpty(testProfileId)) {
//            TestProfile testProfile = testProfileRepository.findByRecordId(testProfileId);
//            if (testProfile != null) {
//               // List<ProfileLocatorDto> profileLocatorList = testProfile.getProfileLocators();
//                if (CollectionUtils.isNotEmpty(profileLocatorList)) {
//                    List<ProfileLocatorDto> profileLocatorOptional = profileLocatorList.stream().filter(identifier -> identifier.getLocatorId().equals(testLocator.getIdentifier())).collect(Collectors.toList());
//                    if (CollectionUtils.isNotEmpty(profileLocatorOptional)) {
//                        Optional<ProfileLocatorDto> profileLocator = profileLocatorOptional.stream().findFirst();
//                        if (profileLocator.isPresent()) {
//                            inputValue = profileLocator.get().getInputValue();
//                            encryptedInputVal = inputValue;
//                            inputValue = inputValue.contains(CONSTANT_VAL) ? decrypt(inputValue.replaceAll(CONSTANT_VAL, "")) : inputValue;
//                        }
//                    }
//                }
//            }
//        }
//        WebElement element = selectorService.getUiElement(context, testLocator);
//        if (element == null) {
//            Media media = reportAction(context, element, testLocator.getDescription() + " Data:" + encryptedInputVal, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
//            ActionResult actionResult = new ActionResult();
//            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data:" + encryptedInputVal, media != null ? media.getPath() : null, Status.FAIL);
//            return actionResult;
//        }

//        element.clear();
//        ActionResult actionResult = new ActionResult();
//        LocatorSelectorDto locatorSelectorDto = testLocator.getUiLocatorSelector(itemSite);
//        String inputData = locatorSelectorDto != null ? locatorSelectorDto.getInputData():"";
//        //TODO check if encryption is correctly working
//        if (StringUtils.isNotEmpty(inputValue)) {
//            Media media = reportAction(context, element, testLocator.getDescription() + " Data:" + encryptedInputVal, testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
//            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data:" + encryptedInputVal, media != null ? media.getPath() : null, Status.PASS);
//            element.sendKeys(inputValue);
//        } else if (BooleanUtils.isTrue(locatorPriority.getIsContextData())) {
//            Media media = reportAction(context, element, testLocator.getDescription() + " Data:" + threadTestContext.getData().get(testLocator.getInputDataEncrypted(itemSite)), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
//            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data:" + threadTestContext.getData().get(testLocator.getInputDataEncrypted(itemSite)), media != null ? media.getPath() : null, Status.PASS);
//            element.sendKeys(threadTestContext.getData().get(inputData));
//        } else {
//            Media media = reportAction(context, element, testLocator.getDescription() + " Data:" + testLocator.getInputDataEncrypted(itemSite), testLocator.getIdentifier(), locatorGroupData.isTakeAScreenshot());
//            //actionResult.setActionResult(testLocator.getIdentifier(), testLocator.getDescription() + " Data:" + testLocator.getInputDataEncrypted(itemSite), media != null ? media.getPath() : null, Status.PASS);
//            element.sendKeys(inputData);
//        }
//        return actionResult;
        return null;
    }
    }
