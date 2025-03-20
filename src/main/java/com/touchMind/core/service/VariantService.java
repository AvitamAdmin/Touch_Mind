package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.VariantWsDto;

public interface VariantService {
    VariantWsDto handleEdit(VariantWsDto request);
}
