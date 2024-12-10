package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.CatalogWsDto;

public interface CatalogService {
    CatalogWsDto handleEdit(CatalogWsDto request);
}
