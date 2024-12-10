package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.MediaWsDto;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaWsDto handleEdit(MultipartFile request);
}
