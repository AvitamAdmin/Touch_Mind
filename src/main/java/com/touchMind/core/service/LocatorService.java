package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.model.TestLocator;
import com.touchMind.form.LocatorForm;

import java.util.List;

public interface LocatorService {
    List<TestLocator> getLocators();

    LocatorForm editLocator(String locatorId);

    void deleteLocator(String locatorId);

    TestLocator getLocatorById(String locatorId);

    List<String> getMethodNames();

    void getFormWithSiteMap(LocatorForm locatorForm);

    void saveLocator(LocatorForm locatorForm);

    List<TestLocatorGroupDto> getLocatorGroups(String recordId);
}
