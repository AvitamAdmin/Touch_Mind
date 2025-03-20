package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.MediaWsDto;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaWsDto handleEdit(MultipartFile request);
}
