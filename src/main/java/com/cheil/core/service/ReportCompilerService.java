package com.cheil.core.service;

import com.cheil.core.mongo.dto.ReportCompilerWsDto;

public interface ReportCompilerService {
    ReportCompilerWsDto handleEdit(ReportCompilerWsDto request);
}
