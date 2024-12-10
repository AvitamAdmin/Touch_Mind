package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.SourceTargetMappingWsDto;

public interface SourceTargetMappingService {
    SourceTargetMappingWsDto handleEdit(SourceTargetMappingWsDto request);
}
