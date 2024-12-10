package com.cheil.core.service;

import com.cheil.core.mongo.dto.SystemWsDto;

public interface SystemService {
    SystemWsDto handleEdit(SystemWsDto request);
}
