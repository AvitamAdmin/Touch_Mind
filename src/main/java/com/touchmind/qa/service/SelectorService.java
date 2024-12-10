package com.touchmind.qa.service;

import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorSelectorDto;
import com.touchmind.qa.framework.ThreadTestContext;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;

import java.util.List;

public interface SelectorService {
    WebElement getUiElement(ITestContext context, TestLocator locator);

    List<WebElement> getUiElements(ITestContext context, TestLocator locator);

    WebElement getBy(ThreadTestContext threadTestContext, LocatorSelectorDto selector);
}
