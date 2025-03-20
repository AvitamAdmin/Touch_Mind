package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestLocatorGroupWsDto;

public interface TestLocatorGroupService {

    TestLocatorGroupWsDto handleEdit(TestLocatorGroupWsDto request);
}
