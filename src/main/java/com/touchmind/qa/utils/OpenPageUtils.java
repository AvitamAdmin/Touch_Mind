package com.touchmind.qa.utils;

import com.touchmind.qa.framework.ThreadTestContext;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestContext;


public class OpenPageUtils {
    public static final String URL_INFO_MESSAGE = "URL: \"%s\".";
    public static final String PERIOD = ".";
    public static final String OPENING_PAGE_MESSAGE = "%s \"%s\"";
    public static final String OPENING_PAGE = "opening.page";
    public static final String OPENED = "opened";
    public static final String OPENED_MESSAGE = "%s\"%s\".";


    public static void openUrl(ITestContext context, String url) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        if (StringUtils.isEmpty(url)) return;
        ReportUtils.info(context, String.format(URL_INFO_MESSAGE, url), StringUtils.EMPTY, false);
        threadTestContext.getDriver().get(url);
    }

    public static void openUrl(ITestContext context, String url, String pageName, boolean takeAScreenshot) {
        ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
        if (StringUtils.isEmpty(url)) return;

        threadTestContext.getDriver().get(url);
        ReportUtils.info(context,
                String.format(URL_INFO_MESSAGE, url), StringUtils.EMPTY,
                takeAScreenshot);
    }

    public static String constructUrl(String env, String url, String environmentField) {
        if (!StringUtils.equals(env, TestDataUtils.PRD)) {
            return url.replace(environmentField, env);
        }
        return url.replace(environmentField + PERIOD, StringUtils.EMPTY);
    }
}
