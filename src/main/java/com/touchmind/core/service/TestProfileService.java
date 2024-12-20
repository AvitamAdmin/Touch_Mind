package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestProfileWsDto;

import java.util.List;

public interface TestProfileService {
  //  List<ProfileLocatorDto> getProfileLocators();

    void deleteTestProfile(String id);

    TestProfileWsDto handleCopy(TestProfileWsDto request);

    TestProfileWsDto handleEdit(TestProfileWsDto request);
}
