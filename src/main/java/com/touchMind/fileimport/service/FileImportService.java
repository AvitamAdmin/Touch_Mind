package com.touchMind.fileimport.service;


import com.touchMind.core.mongo.dto.CommonWsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileImportService {
    Boolean importFile(MultipartFile file, String entityType, String repositoryName, String modelName, CommonWsDto commonWsDto) throws IOException;
}
