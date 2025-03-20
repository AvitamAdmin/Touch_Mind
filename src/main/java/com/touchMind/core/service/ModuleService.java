package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ModuleWsDto;

public interface ModuleService {

    ModuleWsDto handleEdit(ModuleWsDto request);
}
