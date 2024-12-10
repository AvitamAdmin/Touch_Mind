package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestDataTypeWsDto;

public interface TestDataTypeService {
    TestDataTypeWsDto handleEdit(TestDataTypeWsDto request);
}
