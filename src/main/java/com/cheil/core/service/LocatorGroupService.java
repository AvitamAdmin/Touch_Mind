package com.cheil.core.service;

import com.cheil.core.mongo.dto.LocatorGroupDto;
import com.cheil.core.mongo.model.TestLocatorGroup;

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
