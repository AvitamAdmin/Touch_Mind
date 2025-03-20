package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestLocatorWsDto;

public interface TestLocatorService {

    TestLocatorWsDto handleEdit(TestLocatorWsDto request);
}
