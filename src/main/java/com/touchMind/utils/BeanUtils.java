package com.touchMind.utils;

import com.touchMind.core.SpringContext;
import com.touchMind.core.mongotemplate.repository.QARepository;
import com.touchMind.core.service.LocatorGroupService;
import com.touchMind.core.service.LocatorService;
import com.touchMind.core.service.MessageResourceService;
import com.touchMind.core.service.TestPlanService;
import com.touchMind.qa.framework.ExtentManager;
import com.touchMind.qa.service.QualityAssuranceService;
import com.touchMind.qa.service.SelectorService;
import com.touchMind.qa.strategies.ActionFactory;
import com.touchMind.qa.strategies.UrlFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;

public class BeanUtils {
    public static Environment getEnv() {
        return getFactory(Environment.class);
    }

    public static UrlFactory getUrlFactory() {
        return getFactory(UrlFactory.class);
    }

    public static ExtentManager getExtentManager() {
        return getFactory(ExtentManager.class);
    }

    public static ActionFactory getActionFactory() {
        return getFactory(ActionFactory.class);
    }

    public static SelectorService getSelectorService() {
        return getFactory(SelectorService.class);
    }

    public static TestPlanService getTestPlanService() {
        return getFactory(TestPlanService.class);
    }

    public static LocatorGroupService getTestLocatorGroupService() {
        return getFactory(LocatorGroupService.class);
    }

    public static QualityAssuranceService getQualityAssuranceService() {
        return getFactory(QualityAssuranceService.class);
    }

    public static MessageResourceService getMessageResourceService() {
        return getFactory(MessageResourceService.class);
    }

    public static MessageSource getMessageResource() {
        return getFactory(MessageSource.class);
    }

    public static LocatorService getLocatorService() {
        return getFactory(LocatorService.class);
    }

    public static String getLocalizedString(String key) {
        return getMessageResource().getMessage(key, null, LocaleContextHolder.getLocale());
    }

    protected static <T> T getFactory(Class<T> clazz) {
        return SpringContext.getBean(clazz);
    }

    public static QARepository getQaRepository() {
        return getFactory(QARepository.class);
    }

}
