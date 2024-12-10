package com.cheil.core.service;

import com.cheil.core.mongo.dto.DataSourceWsDto;

public interface DataSourceService {

    DataSourceWsDto handleEdit(DataSourceWsDto request);
}
