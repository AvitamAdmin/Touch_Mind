package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.CatalogWsDto;

public interface CatalogService {
    CatalogWsDto handleEdit(CatalogWsDto request);
}
