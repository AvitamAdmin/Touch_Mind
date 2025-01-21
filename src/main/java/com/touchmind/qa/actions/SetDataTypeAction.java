package com.touchmind.qa.actions;

import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.core.mongo.repository.TestDataSubtypeRepository;
import com.touchmind.core.mongo.repository.TestDataTypeRepository;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.service.ActionRequest;
import com.touchmind.qa.service.ActionResult;
import com.touchmind.qa.service.ElementActionService;
import com.touchmind.qa.strategies.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.SET_DATA_TYPE_ACTION)
public class SetDataTypeAction implements ElementActionService {

    public static final String PAYMENT = "Payment";
    public static final String DELIVERY = "Delivery";

//    @Autowired
//    TestDataTypeRepository testDataTypeRepository;
//    @Autowired
//    TestDataSubtypeRepository testDataSubtypeRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        return null;
    }
}

//    @Override
//    public ActionResult performAction(ActionRequest actionRequest) {
//        ITestContext context = actionRequest.getContext();
//        TestLocator locator = actionRequest.getTestLocator();
//        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
//        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
//        String dataType = locator.getTestDataType();
//        if (StringUtils.isNotEmpty(dataType)) {
//            String dataTypeVal = testDataTypeRepository.findById(new ObjectId(dataType)).get().getIdentifier();
//            if (StringUtils.isNotEmpty(locator.getTestDataSubtype())) {
//                if (PAYMENT.equalsIgnoreCase(dataTypeVal)) {
//                    testData.put(TestDataUtils.Field.PAYMENT_TYPE.toString(), testDataSubtypeRepository.findById(new ObjectId(locator.getTestDataSubtype())).get().getIdentifier());
//                }
//                if (DELIVERY.equalsIgnoreCase(dataTypeVal)) {
//                    testData.put(TestDataUtils.Field.DELIVERY_TYPE.toString(), testDataSubtypeRepository.findById(new ObjectId(locator.getTestDataSubtype())).get().getIdentifier());
//                }
//            }
//        }
//        context.getSuite().setAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString(), testData);
//        ActionResult actionResult = new ActionResult();
//        //actionResult.setActionResult(locator.getIdentifier(), locator.getDescription(), null, Status.INFO);
//        return actionResult;
//    }
//}
