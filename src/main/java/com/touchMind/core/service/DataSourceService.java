package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.DataSourceWsDto;

public interface DataSourceService {

    DataSourceWsDto handleEdit(DataSourceWsDto request);
}
