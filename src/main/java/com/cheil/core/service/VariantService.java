package com.cheil.core.service;

import com.cheil.core.mongo.dto.VariantWsDto;

public interface VariantService {
    VariantWsDto handleEdit(VariantWsDto request);
}
