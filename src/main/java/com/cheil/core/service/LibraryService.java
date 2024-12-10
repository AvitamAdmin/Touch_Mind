package com.cheil.core.service;

import com.cheil.core.mongo.dto.LibraryWsDto;

public interface LibraryService {
    LibraryWsDto handleEdit(LibraryWsDto request);
}
