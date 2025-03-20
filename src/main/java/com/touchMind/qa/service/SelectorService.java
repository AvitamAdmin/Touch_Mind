package com.touchMind.qa.service;

import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.form.LocatorSelectorDto;
import com.touchMind.qa.framework.ThreadTestContext;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;

import java.util.List;

public interface SelectorService {
    WebElement getUiElement(ITestContext context, TestLocator locator);

    List<WebElement> getUiElements(ITestContext context, TestLocator locator);

    WebElement getBy(ThreadTestContext threadTestContext, LocatorSelectorDto selector, String methodName, ITestContext context);
}
