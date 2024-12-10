package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ReportDto;

import java.io.InputStream;

public interface JsonService {
    boolean processJsonData(InputStream inputStream, ReportDto reportDto);

    boolean processJsonData(String json, ReportDto reportDto);
}
