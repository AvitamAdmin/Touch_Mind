package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.TestLocatorGroupWsDto;

public interface TestLocatorGroupService {

    TestLocatorGroupWsDto handleEdit(TestLocatorGroupWsDto request);
}
