package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.ReportCompilerWsDto;

public interface ReportCompilerService {
    ReportCompilerWsDto handleEdit(ReportCompilerWsDto request);
}
