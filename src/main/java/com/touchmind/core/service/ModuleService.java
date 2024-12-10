package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ModuleWsDto;

public interface ModuleService {

    ModuleWsDto handleEdit(ModuleWsDto request);
}
