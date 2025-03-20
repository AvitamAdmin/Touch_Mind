package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SystemWsDto;

public interface SystemService {
    SystemWsDto handleEdit(SystemWsDto request);
}
