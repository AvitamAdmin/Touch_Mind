package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ReportDto;

import java.io.InputStream;

public interface JsonService {
    boolean processJsonData(InputStream inputStream, ReportDto reportDto);

    boolean processJsonData(String json, ReportDto reportDto);
}
