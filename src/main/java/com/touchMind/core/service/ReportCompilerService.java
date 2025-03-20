package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.ReportCompilerWsDto;

public interface ReportCompilerService {
    ReportCompilerWsDto handleEdit(ReportCompilerWsDto request);
}
