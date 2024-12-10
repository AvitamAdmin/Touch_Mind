package com.cheil.qa.utils;

import com.cheil.qa.framework.ThreadTestContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class ScrollUtils {
    public static final String JAVASCRIPT_SCROLL_INTO_VIEW = "arguments[0].scrollIntoView();";

    public static void scrollIntoView(ThreadTestContext context, WebElement element) {
        if (element == null) return;

        ((JavascriptExecutor) context.getDriver()).executeScript(JAVASCRIPT_SCROLL_INTO_VIEW, element);
    }
}
