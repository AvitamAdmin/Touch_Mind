package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.dto.TestLocatorDto;
import com.touchmind.core.mongo.model.TestLocator;

import java.util.List;
import java.util.Map;

public interface LocatorService {
    List<TestLocator> getLocators();

    TestLocatorDto editLocator(String locatorId);

    boolean addLocator(TestLocatorDto locator);

    void deleteLocator(String locatorId);

    TestLocator getLocatorById(String locatorId);

    List<String> getMethodNames();

    List<LocatorGroupDto> getLocatorGroups(String locatorIdentifier);

    Map<String, Long> getLocatorGroups();
}
