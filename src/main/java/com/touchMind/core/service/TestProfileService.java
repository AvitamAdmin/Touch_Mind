package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ProfileLocatorDto;
import com.touchMind.core.mongo.dto.TestProfileWsDto;

import java.util.List;

public interface TestProfileService {
    List<ProfileLocatorDto> getProfileLocators();

    void deleteTestProfile(String id);

    TestProfileWsDto handleCopy(String recordId);

    TestProfileWsDto handleEdit(TestProfileWsDto request);
}
