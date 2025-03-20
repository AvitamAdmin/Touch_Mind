package com.touchMind.qa.framework;

import com.touchMind.qa.utils.ReportUtils;
import com.touchMind.qa.utils.TestDataUtils;
import com.touchMind.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.SkipException;

import static com.touchMind.qa.utils.QaConstants.CURRENT_TEST_METHOD;

public class CustomTestListener implements ITestListener {
    public static final String EXECUTING_PAGE_S_AND_METHOD_S = "executing.page.s.and.method.s";
    public static final String SKIPPED_TEST_AS_MAX_EXCEPTION_COUNT_REACHED = "skipped.test.as.max.exception.count.reached";
    private static int exceptionCount;
    private final int maxExceptions;
    Logger LOG = LoggerFactory.getLogger(CustomTestListener.class);

    public CustomTestListener(int aMaxExceptions) {
        maxExceptions = aMaxExceptions;
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (exceptionCount >= maxExceptions) {
            LOG.error(SKIPPED_TEST_AS_MAX_EXCEPTION_COUNT_REACHED);
            throw new SkipException(SKIPPED_TEST_AS_MAX_EXCEPTION_COUNT_REACHED);
        }
        ITestContext iTestContext = result.getTestContext();
        ReportUtils.info(iTestContext,
                String.format(BeanUtils.getLocalizedString(EXECUTING_PAGE_S_AND_METHOD_S),
                        result.getMethod().getRealClass().getSimpleName(),
                        result.getMethod().getMethodName()), StringUtils.EMPTY,
                false);

        // Set the method name as an attribute in ITestContext
        result.getTestContext().setAttribute(CURRENT_TEST_METHOD, result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        exceptionCount++;

        ReportUtils.reportError((ITestContext) result.getTestContext().getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString()),
                result.getMethod().getMethodName(), StringUtils.EMPTY,
                result.getThrowable(), false);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
        exceptionCount = 0;
    }

    @Override
    public void onFinish(ITestContext context) {
        if (exceptionCount >= maxExceptions) {
            ThreadTestContext threadTestContext = (ThreadTestContext) context.getAttribute(TestDataUtils.Field.THREAD_CONTEXT.toString());
            ReportUtils.fail(context,
                    BeanUtils.getLocalizedString(SKIPPED_TEST_AS_MAX_EXCEPTION_COUNT_REACHED), StringUtils.EMPTY,
                    false);
        }

    }
}