package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.LocatorGroupDto;
import com.touchmind.core.mongo.model.TestLocatorGroup;

import java.util.List;

public interface LocatorGroupService {
    List<TestLocatorGroup> getLocatorsGroup();

    TestLocatorGroup findLocatorByGroupId(String locatorGroupId);

    LocatorGroupDto editLocatorGroup(String locatorId);

    boolean addLocatorGroup(LocatorGroupDto locator);

    void deleteLocatorGroup(String locatorId);

    List<String> getConditionOperators();

    List<TestLocatorGroup> getLocatorsGroupOrderByIdentifier();
}
