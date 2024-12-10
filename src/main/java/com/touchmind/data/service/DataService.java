package com.touchmind.data.service;

import com.touchmind.core.mongo.dto.ReportDto;

public interface DataService {
    String getType();

    boolean processApi(ReportDto reportDto, String api);
}
