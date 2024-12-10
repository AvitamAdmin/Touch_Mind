package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestDataSubtypeWsDto;

public interface TestDataSubTypeService {
    TestDataSubtypeWsDto handleEdit(TestDataSubtypeWsDto request);
}
