package com.cheil.core.service;

import com.cheil.core.mongo.dto.MediaWsDto;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaWsDto handleEdit(MultipartFile request);
}
