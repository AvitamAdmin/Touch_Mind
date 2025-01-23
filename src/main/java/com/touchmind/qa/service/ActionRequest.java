package com.touchmind.qa.service;

import com.touchmind.core.mongo.model.LocatorPriority;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorGroupData;
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
}
