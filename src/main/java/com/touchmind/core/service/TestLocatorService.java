package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestLocatorWsDto;

public interface TestLocatorService {

    TestLocatorWsDto handleEdit(TestLocatorWsDto request);
}
