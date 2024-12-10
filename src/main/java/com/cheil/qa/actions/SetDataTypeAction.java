package com.cheil.qa.actions;

import com.cheil.core.mongo.model.TestLocator;
import com.cheil.core.mongo.repository.TestDataSubtypeRepository;
import com.cheil.core.mongo.repository.TestDataTypeRepository;
import com.cheil.form.LocatorGroupData;
import com.cheil.qa.service.ActionRequest;
import com.cheil.qa.service.ActionResult;
import com.cheil.qa.service.ElementActionService;
import com.cheil.qa.strategies.ActionType;
import com.cheil.qa.utils.TestDataUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.ITestContext;

@Service(ActionType.SET_DATA_TYPE_ACTION)
public class SetDataTypeAction implements ElementActionService {

    public static final String PAYMENT = "Payment";
    public static final String DELIVERY = "Delivery";

    @Autowired
    TestDataTypeRepository testDataTypeRepository;
    @Autowired
    TestDataSubtypeRepository testDataSubtypeRepository;

    @Override
    public ActionResult performAction(ActionRequest actionRequest) {
        ITestContext context = actionRequest.getContext();
        TestLocator locator = actionRequest.getTestLocator();
        LocatorGroupData locatorGroupData = actionRequest.getLocatorGroupData();
        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());
        String dataType = locator.getTestDataType();
        if (StringUtils.isNotEmpty(dataType)) {
            String dataTypeVal = testDataTypeRepository.findById(new ObjectId(dataType)).get().getIdentifier();
            if (StringUtils.isNotEmpty(locator.getTestDataSubtype())) {
                if (PAYMENT.equalsIgnoreCase(dataTypeVal)) {
                    testData.put(TestDataUtils.Field.PAYMENT_TYPE.toString(), testDataSubtypeRepository.findById(new ObjectId(locator.getTestDataSubtype())).get().getIdentifier());
                }
                if (DELIVERY.equalsIgnoreCase(dataTypeVal)) {
                    testData.put(TestDataUtils.Field.DELIVERY_TYPE.toString(), testDataSubtypeRepository.findById(new ObjectId(locator.getTestDataSubtype())).get().getIdentifier());
                }
            }
        }
        context.getSuite().setAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString(), testData);
        ActionResult actionResult = new ActionResult();
        //actionResult.setActionResult(locator.getIdentifier(), locator.getDescription(), null, Status.INFO);
        return actionResult;
    }
}
