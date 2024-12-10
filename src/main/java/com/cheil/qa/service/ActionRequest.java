package com.cheil.qa.service;

import com.cheil.core.mongo.model.LocatorPriority;
import com.cheil.core.mongo.model.TestLocator;
import com.cheil.form.LocatorGroupData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.testng.ITestContext;

@Getter
@Setter
@NoArgsConstructor
public class ActionRequest {
    private ITestContext context;
    private LocatorGroupData locatorGroupData;
    private TestLocator testLocator;
    private LocatorPriority locatorPriority;
    private ObjectId qaTestResultId;
}
