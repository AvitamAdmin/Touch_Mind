package com.touchMind.core.service;


import com.touchMind.core.mongo.dto.ReportsMapperWsDto;
import com.touchMind.core.mongo.model.ReportsMapper;

import java.util.List;

public interface ReportsMapperService {
    List<ReportsMapper> findByStatusOrderByIdentifier(Boolean status);

    ReportsMapperWsDto handleEdit(ReportsMapperWsDto request);
}
