package com.cheil.core.service;

import com.cheil.core.mongo.dto.TestLocatorWsDto;

public interface TestLocatorService {

    TestLocatorWsDto handleEdit(TestLocatorWsDto request);
}
