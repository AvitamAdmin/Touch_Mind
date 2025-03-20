package com.touchMind.qa.framework;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.touchMind.qa.utils.TestDataUtils.Field;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class QaConfig {
    public static final String WEBDRIVER_CHROME_OPTION_EXTENSION = "webdriver.chrome.option.extension";
    public static final String EXTENT_REPORT_TITLE = "extent.report.title";
    public static final String REGEX_WHITESPACE = "\\s+";
    private static final String WEBDRIVER_CHROME_OPTIONS = "webdriver.chrome.options";
    private static final String WEBDRIVER_CHROME_OPTIONS_BINARY = "webdriver.chrome.options.binary";
    private static final ViewName[] EXTENT_REPORT_VIEW_NAMES = new ViewName[]{ViewName.DASHBOARD, ViewName.TEST};
    Logger LOG = LoggerFactory.getLogger(QaConfig.class);
    @Value("${testng.webdriver.wait.timeout.seconds}")
    int webDriverWaitTimeoutSeconds;
    @Value("${testng.webdriver.wait.polling.seconds}")
    int webDriverWaitPollingSeconds;
    @Value("${testng.max.exceptions.before.aborting.test}")
    int maxExceptionsBeforeAbortingTest;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private Environment env;
    @Value("${extent.report.timestamp.format}")
    private String extentReportTimestampFormat;
    @Value("${extent.report.custom.js.code}")
    private String extentReportCustomJsCode;

    @Bean
    @Scope("prototype")
    public TestNG xmlSuite(String testData) {
        XmlSuite suite = new XmlSuite();
        suite.setParameters(Collections.singletonMap(Field.TESTNG_CONTEXT_PARAM_NAME.toString(), testData));
        suite.setParallel(XmlSuite.ParallelMode.NONE);
        suite.setName("Zero In Test Suite initialized");
        XmlTest zeroInTest = new XmlTest(suite);
        zeroInTest.setName("Zero in test class initialized");
        List<XmlClass> testClasses = new ArrayList<>();
        // Set params if needed
        //zeroInTest.setParameters(testngParams);
        testClasses.add(new XmlClass("com.touchMind.qa.testplans.CoreTest"));
        zeroInTest.setXmlClasses(testClasses);
        //Create a list of XmlTests and add the Xmltest you created earlier to it.
        List<XmlTest> allTests = new ArrayList<XmlTest>();
        allTests.add(zeroInTest);
        suite.setTests(allTests);
        TestNG testNG = new TestNG();
        testNG.setXmlSuites(List.of(suite));
        return testNG;
    }

    @Bean
    @Scope("prototype")
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().driverVersion("120.0.6099.109").setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        String chromeOption = env.getProperty(WEBDRIVER_CHROME_OPTIONS);
        LOG.info(chromeOption);
        if (StringUtils.isNotEmpty(chromeOption)) {
            for (String arg : chromeOption.split(REGEX_WHITESPACE)) {
                chromeOptions.addArguments(arg);
            }
        }
        String chromeOptionsBinary = env.getProperty(WEBDRIVER_CHROME_OPTIONS_BINARY);
        if (StringUtils.isNotEmpty(chromeOptionsBinary)) {
            chromeOptions.setBinary(chromeOptionsBinary);
        }
        String extensionPath = env.getProperty(WEBDRIVER_CHROME_OPTION_EXTENSION);
        if (StringUtils.isNotEmpty(extensionPath)) {
            Path currentRelativePath = Paths.get(extensionPath);
            chromeOptions.addExtensions(new File(currentRelativePath.toAbsolutePath().toString()));
        }
        return new ChromeDriver(chromeOptions);
    }

    @Bean
    @Scope("prototype")
    public FluentWait<WebDriver> fluentWait(WebDriver driver) {
        return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(webDriverWaitTimeoutSeconds)).pollingEvery(Duration.ofSeconds(webDriverWaitPollingSeconds)).ignoring(NoSuchElementException.class);
    }

    @Bean
    @Scope("prototype")
    public ExtentReports extentReports(String reportPath) {
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        ExtentSparkReporterConfig config = spark.config();
        config.setDocumentTitle(messageSource.getMessage(EXTENT_REPORT_TITLE, null, LocaleContextHolder.getLocale()));
        config.setTimeStampFormat(extentReportTimestampFormat);
        config.setJs(extentReportCustomJsCode);
        spark.viewConfigurer().viewOrder().as(EXTENT_REPORT_VIEW_NAMES).apply();

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(spark);

        return extentReports;
    }

    @Bean
    @Scope("prototype")
    public AtomicInteger atomicInteger() {
        return new AtomicInteger();
    }
}
