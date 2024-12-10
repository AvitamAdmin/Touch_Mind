package com.cheil.data.service;

import com.cheil.core.mongo.dto.ReportDto;

public interface DataService {
    String getType();

    boolean processApi(ReportDto reportDto, String api);
}
