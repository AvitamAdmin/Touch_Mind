package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestDataSubtypeWsDto;

public interface TestDataSubTypeService {
    TestDataSubtypeWsDto handleEdit(TestDataSubtypeWsDto request);
}
