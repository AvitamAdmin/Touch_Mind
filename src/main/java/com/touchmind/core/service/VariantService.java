package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.VariantWsDto;

public interface VariantService {
    VariantWsDto handleEdit(VariantWsDto request);
}
