package com.cheil.core.service;

import com.cheil.core.mongo.dto.ProfileLocatorDto;
import com.cheil.core.mongo.dto.TestProfileWsDto;

import java.util.List;

public interface TestProfileService {
    List<ProfileLocatorDto> getProfileLocators();

    void deleteTestProfile(String id);

    TestProfileWsDto handleCopy(TestProfileWsDto request);

    TestProfileWsDto handleEdit(TestProfileWsDto request);
}
