package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.TestDataTypeWsDto;

public interface TestDataTypeService {
    TestDataTypeWsDto handleEdit(TestDataTypeWsDto request);
}
