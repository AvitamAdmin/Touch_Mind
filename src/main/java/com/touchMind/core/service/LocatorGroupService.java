package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestLocatorGroupDto;
import com.touchMind.core.mongo.model.TestLocatorGroup;

import java.util.List;

public interface LocatorGroupService {
    List<TestLocatorGroup> getLocatorsGroup();

    TestLocatorGroup findLocatorByGroupId(String locatorGroupId);

    TestLocatorGroupDto editLocatorGroup(String locatorId);

    boolean addLocatorGroup(TestLocatorGroupDto locator);

    void deleteLocatorGroup(String locatorId);

    List<String> getConditionOperators();

    List<TestLocatorGroup> getLocatorsGroupOrderByIdentifier();
}
