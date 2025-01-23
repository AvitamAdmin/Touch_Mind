package com.touchmind.fileimport.service;


import com.touchmind.core.mongo.dto.CommonWsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileImportService {
    Boolean importFile(MultipartFile file, String entityType, String repositoryName, String modelName, CommonWsDto commonWsDto) throws IOException;
}
