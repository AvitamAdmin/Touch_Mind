package com.touchMind.data.service;

import com.touchMind.core.mongo.dto.ReportDto;

public interface DataService {
    String getType();

    boolean processApi(ReportDto reportDto, String api);
}
