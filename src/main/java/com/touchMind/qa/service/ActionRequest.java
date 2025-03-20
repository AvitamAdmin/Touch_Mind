package com.touchMind.qa.service;

import com.touchMind.core.mongo.model.LocatorPriority;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.form.LocatorGroupData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.testng.ITestContext;

@Getter
@Setter
@NoArgsConstructor
public class ActionRequest extends BaseRequest {
    private ITestContext context;
    private LocatorGroupData locatorGroupData;
    private TestLocator testLocator;
    private Object sku;
    private LocatorPriority locatorPriority;
    private ObjectId qaTestResultId;
    private String testCaseId;
}
