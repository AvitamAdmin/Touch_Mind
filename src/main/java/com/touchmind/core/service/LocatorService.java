package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.model.TestLocator;
import com.touchmind.form.LocatorForm;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface LocatorService {
    List<TestLocator> getLocators();

    LocatorForm editLocator(String locatorId);

    boolean addLocator(LocatorForm locator);

    void deleteLocator(String locatorId);

    TestLocator getLocatorById(ObjectId locatorId);

    List<String> getMethodNames();

    void getFormWithSiteMap(LocatorForm locatorForm);

    List<LocatorGroupDto> getLocatorGroups(String locatorIdentifier);

    Map<String, Long> getLocatorGroups();

    void saveLoctor(LocatorForm locatorForm);
}
