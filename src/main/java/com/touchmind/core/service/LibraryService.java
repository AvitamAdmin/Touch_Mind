package com.touchmind.core.service;

import com.touchmind.core.mongo.dto.LibraryWsDto;

public interface LibraryService {
    LibraryWsDto handleEdit(LibraryWsDto request);
}
