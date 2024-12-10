package com.cheil.core.service;

import com.cheil.core.mongo.dto.TestDataTypeWsDto;

public interface TestDataTypeService {
    TestDataTypeWsDto handleEdit(TestDataTypeWsDto request);
}
