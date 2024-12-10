package com.cheil.core.service;

import com.cheil.core.mongo.dto.ReportDto;

import java.io.InputStream;

public interface JsonService {
    boolean processJsonData(InputStream inputStream, ReportDto reportDto);

    boolean processJsonData(String json, ReportDto reportDto);
}
