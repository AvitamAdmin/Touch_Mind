package com.touchmind.qa.pages.concretepages.testPlans;

import com.touchmind.core.SpringContext;
import com.touchmind.core.mongo.model.*;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.core.service.TestPlanService;
import com.touchmind.form.LocatorGroupData;
import com.touchmind.qa.framework.ExtentManager;
import com.touchmind.qa.framework.QaConfig;
import com.touchmind.qa.framework.ThreadTestContext;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionFactory;
import com.touchmind.qa.strategies.ActionType;
import com.touchmind.qa.strategies.UrlFactory;
import com.touchmind.qa.utils.TestDataUtils;
import com.touchmind.qa.utils.TestDataUtils.Field;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.lang.System;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public abstract class AbstractTestPlan {
    public static final String DEFAULT_URL_SERVICE_TYPE = "DEFAULT";
    public static final String INITIALIZED_DRIVER_AND_WAIT = "initialized driver and fluentWait";
    private static final Map<String, String> subsidiaryUrlServiceMappings = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(QaConfig.class);
    protected QualityAssuranceService qualityAssuranceService;
    protected SelectorService selectorService;
    protected TestPlanService testPlanService;
    protected LocatorGroupService locatorGroupService;
    protected UrlFactory urlFactory;
    protected ActionFactory actionFactory;
    private Environment env;
    private CoreService coreService;

    protected static String getUrlServiceType(String subsidiary) {
        if (subsidiaryUrlServiceMappings.containsKey(subsidiary)) {
            return subsidiaryUrlServiceMappings.get(subsidiary);
        }
        return DEFAULT_URL_SERVICE_TYPE;
    }

    @BeforeSuite
    public void beforeSuite(ITestContext context) {
        String data = context.getSuite().getXmlSuite().getAllParameters().entrySet().iterator().next().getValue();
        JSONObject jsonObject = new JSONObject(data);
        context.getSuite().setAttribute(Field.TESTNG_CONTEXT_PARAM_NAME.toString(), jsonObject);
        context.getSuite().setAttribute(Field.ATOMIC_COUNTER.toString(), SpringContext.getBean(AtomicInteger.class));
        ExtentManager extentManager = getFactory(null, ExtentManager.class);
        context.getSuite().setAttribute(Field.EXTENT_MANAGER.toString(), extentManager);

        //set here the session ID for all tests
        UUID uuid = UUID.randomUUID();
        String sessionId = uuid.toString();
        context.getSuite().setAttribute(Field.SESSION_ID.toString(), sessionId);

        JSONObject testData = (JSONObject) context.getSuite().getAttribute(TestDataUtils.Field.TESTNG_CONTEXT_PARAM_NAME.toString());

        setTestDataJson(testData, context);
    }

    @BeforeTest
    public void setupData(ITestContext context) {
        ThreadTestContext threadContext = SpringContext.getBean(ThreadTestContext.class);
        try {
            AtomicInteger atomicInteger = (AtomicInteger) context.getSuite().getAttribute(Field.ATOMIC_COUNTER.toString());
            threadContext.setTestIdentifier(atomicInteger.getAndIncrement());
            LOG.info(INITIALIZED_DRIVER_AND_WAIT);
            context.setAttribute(Field.THREAD_CONTEXT.toString(), threadContext);
        } catch (Exception e) {
            LOG.error(e.getMessage() + e + System.lineSeparator() + Arrays.toString(Thread.currentThread().getStackTrace()));
        }
    }

    protected <T> T getFactory(T factory, Class<T> clazz) {
        if (factory == null) {
            factory = SpringContext.getBean(clazz);
        }
        return factory;
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite(ITestContext context) {
        getThreadContext(context).getDriver().quit();
        getExtentManager(context).flush();
    }

    protected QualityAssuranceService getQualityAssuranceService() {
        return qualityAssuranceService = getFactory(qualityAssuranceService, QualityAssuranceService.class);
    }

    protected void setTestDataJson(JSONObject testDataJson, ITestContext context) {
        context.getSuite().setAttribute(Field.TESTNG_CONTEXT_PARAM_NAME.toString(), testDataJson);
    }

    protected ThreadTestContext getThreadContext(ITestContext context) {
        return (ThreadTestContext) context.getAttribute(Field.THREAD_CONTEXT.toString());
    }

    protected JSONObject getTestData(ITestContext context) {
        return (JSONObject) context.getSuite().getAttribute(Field.TESTNG_CONTEXT_PARAM_NAME.toString());
    }

    protected ExtentManager getExtentManager(ITestContext context) {
        return (ExtentManager) (context.getSuite().getAttribute(Field.EXTENT_MANAGER.toString()));
    }

    protected SelectorService getSelectorService() {
        return selectorService = getFactory(selectorService, SelectorService.class);
    }

    protected TestPlanService getTestPlanService() {
        return testPlanService = getFactory(testPlanService, TestPlanService.class);
    }

    protected LocatorGroupService getTestLocatorGroupService() {
        return locatorGroupService = getFactory(locatorGroupService, LocatorGroupService.class);
    }

    protected Environment getEnv() {
        return env = getFactory(env, Environment.class);
    }

    protected UrlFactory getUrlFactory() {
        return urlFactory = getFactory(urlFactory, UrlFactory.class);
    }

    protected ActionFactory getActionFactory() {
        return actionFactory = getFactory(actionFactory, ActionFactory.class);
    }

    protected CoreService getCoreService() {
        return coreService = getFactory(coreService, CoreService.class);
    }

    protected String getTestPlanId(ITestContext context) {
        JSONObject testData = getTestData(context);
        return String.valueOf(testData.get(TestDataUtils.Field.TEST_PLAN.toString()));
    }

    protected List<LocatorGroupData> getLocatorsByTestPlan(String objectId) {
        List<LocatorGroupData> testPlans = new ArrayList<>();
        TestPlan testPlan = getTestPlanService().getTestPlanByRecordId(objectId);
        if (CollectionUtils.isEmpty(testPlan.getTestLocatorGroups())) {
            return null;
        }
        List<String> testLocatorGroupList = testPlan.getTestLocatorGroups();
        testLocatorGroupList.stream().forEach(groupId -> {
            TestLocatorGroup testLocatorGroup = getTestLocatorGroupService().findLocatorByGroupId(groupId);
            List<LocatorPriority> locators = testLocatorGroup.getTestLocators();
            List<LocatorPriority> locatorsWithGlobalActions = new ArrayList<>();
            locatorsWithGlobalActions.add(getLocatorPriorityByMethodName(ActionType.ENVIRONMENT_ACTION));
            LocatorGroupData locatorGroupData = new LocatorGroupData();
            if (locators != null) {
                locatorsWithGlobalActions.addAll(locators);
                locatorGroupData.setGroupId(groupId);
                locatorGroupData.setLocatorPriorityList(locatorsWithGlobalActions);
                locatorGroupData.setTakeAScreenshot(BooleanUtils.isTrue(testLocatorGroup.getTakeAScreenshot()));
                testPlans.add(locatorGroupData);
            }
        });
        return testPlans;
    }

    private LocatorPriority getLocatorPriorityByMethodName(String name) {
        LocatorPriority locatorPriority = new LocatorPriority();
        locatorPriority.setLocatorId(name);
        return locatorPriority;
    }

    public QaResultReport initQaResultReport(ITestContext context, JSONObject testData, Object sku) {
        QaResultReport qaResultReport = new QaResultReport();
        Object testName = TestDataUtils.getString(testData, TestDataUtils.Field.TEST_NAME);
        qaResultReport.setTestCaseId(testName.toString());
        qaResultReport.setSku(sku.toString());
        Object cronSessionId = TestDataUtils.getString(testData, TestDataUtils.Field.SESSION_ID);
        String sessionId = ObjectUtils.isNotEmpty(cronSessionId) ? cronSessionId.toString() : (String) context.getSuite().getAttribute(TestDataUtils.Field.SESSION_ID.toString());
        qaResultReport.setSessionId(sessionId);
        qaResultReport.setCreationTime(new Date());
        qaResultReport.setLastModified(new Date());
        qaResultReport.setStatus(true);
        qaResultReport.setCreator(testData.getString("currentUser"));
        qaResultReport.setQaLocatorResultReports(new ArrayList<>());
        return qaResultReport;
    }
}
