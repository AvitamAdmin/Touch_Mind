package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.DataSourceWsDto;

public interface DataSourceService {

    DataSourceWsDto handleEdit(DataSourceWsDto request);
}
