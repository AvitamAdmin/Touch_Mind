package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.LibraryWsDto;

public interface LibraryService {
    LibraryWsDto handleEdit(LibraryWsDto request);
}
