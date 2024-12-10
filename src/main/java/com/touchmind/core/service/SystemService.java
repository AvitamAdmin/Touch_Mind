package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SystemWsDto;

public interface SystemService {
    SystemWsDto handleEdit(SystemWsDto request);
}
