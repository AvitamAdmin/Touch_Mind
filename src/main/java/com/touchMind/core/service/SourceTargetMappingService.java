package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SourceTargetMappingWsDto;

public interface SourceTargetMappingService {
    SourceTargetMappingWsDto handleEdit(SourceTargetMappingWsDto request);
}
