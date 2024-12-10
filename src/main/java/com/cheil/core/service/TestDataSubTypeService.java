package com.cheil.core.service;

import com.cheil.core.mongo.dto.TestDataSubtypeWsDto;

public interface TestDataSubTypeService {
    TestDataSubtypeWsDto handleEdit(TestDataSubtypeWsDto request);
}
