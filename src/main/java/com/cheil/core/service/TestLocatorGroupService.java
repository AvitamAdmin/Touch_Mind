package com.cheil.core.service;

import com.cheil.core.mongo.dto.TestLocatorGroupWsDto;

public interface TestLocatorGroupService {

    TestLocatorGroupWsDto handleEdit(TestLocatorGroupWsDto request);
}
