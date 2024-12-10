package com.cheil.core.service;

import com.cheil.core.mongo.dto.CatalogWsDto;

public interface CatalogService {
    CatalogWsDto handleEdit(CatalogWsDto request);
}
