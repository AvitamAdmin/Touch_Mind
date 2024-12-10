package com.cheil.qa.service;

import com.cheil.core.mongo.model.TestLocator;
import com.cheil.form.LocatorSelectorDto;
import com.cheil.qa.framework.ThreadTestContext;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;

import java.util.List;

public interface SelectorService {
    WebElement getUiElement(ITestContext context, TestLocator locator);

    List<WebElement> getUiElements(ITestContext context, TestLocator locator);

    WebElement getBy(ThreadTestContext threadTestContext, LocatorSelectorDto selector);
}
