package com.cheil.core.service;

import com.cheil.core.mongo.dto.SourceTargetMappingWsDto;

public interface SourceTargetMappingService {
    SourceTargetMappingWsDto handleEdit(SourceTargetMappingWsDto request);
}
