package com.touchmind.utils;

import com.touchmind.core.SpringContext;
import com.touchmind.core.service.LocatorGroupService;
import com.touchmind.core.service.TestPlanService;
import com.touchmind.qa.framework.ExtentManager;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.qa.service.SelectorService;
import com.touchmind.qa.strategies.ActionFactory;
import com.touchmind.qa.strategies.UrlFactory;
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
    public static ExtentManager  getExtentManager(){return  getFactory(ExtentManager.class);}
    public static ActionFactory getActionFactory() {return  getFactory(ActionFactory.class);}
    public static SelectorService getSelectorService() {return getFactory(SelectorService.class);}
    public static TestPlanService getTestPlanService() {return getFactory(TestPlanService.class);}
    public static LocatorGroupService getTestLocatorGroupService() {return getFactory(LocatorGroupService.class);}
    public static QualityAssuranceService getQualityAssuranceService() {return getFactory(QualityAssuranceService.class);}
   // public static MessageResourceService getMessageResourceService() {return getFactory(MessageResourceService.class);}
    public static MessageSource getMessageResource(){return getFactory(MessageSource.class);}
    public static String getLocalizedString(String key) {
        return getMessageResource().getMessage(key, null, LocaleContextHolder.getLocale());
    }
    protected static <T> T getFactory(Class<T> clazz) {
        return SpringContext.getBean(clazz);
    }
}
