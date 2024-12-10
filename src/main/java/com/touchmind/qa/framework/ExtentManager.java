package com.touchmind.qa.framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.touchmind.core.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;


@Component
@Scope("prototype")
public class ExtentManager {
    public static final String STARTING_NEW_REPORT = "Starting a new report: %s%s";
    public static final String EXTENT_HTML = ".html";
    public static final String REPORT_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
            + "resources" + File.separator + "static" + File.separator + "reports" + File.separator;
    private static final Logger LOG = LoggerFactory.getLogger(ExtentManager.class);
    private ExtentReports extentReports;

    public void startNewReport(String reportName) {
        flush();
        LOG.info(String.format(STARTING_NEW_REPORT, REPORT_PATH, reportName));
        extentReports = SpringContext.getBean(ExtentReports.class, REPORT_PATH + reportName + EXTENT_HTML);
    }

    public void flush() {
        if (extentReports != null) {
            extentReports.flush();
            extentReports = null; // Set to null so that a new instance is created next time
        }
    }

    public ExtentTest startNewTest(String testName, String testDescription) {
        return extentReports.createTest(testName, testDescription);
    }
}

