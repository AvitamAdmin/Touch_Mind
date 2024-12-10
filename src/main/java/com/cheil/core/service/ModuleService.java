package com.cheil.core.service;

import com.cheil.core.mongo.dto.ModuleWsDto;

public interface ModuleService {

    ModuleWsDto handleEdit(ModuleWsDto request);
}
