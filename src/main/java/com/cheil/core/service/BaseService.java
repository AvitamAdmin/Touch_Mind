package com.cheil.core.service;

import com.cheil.core.mongo.dto.SavedQueryDto;
import com.cheil.core.mongo.model.CommonFields;

import java.util.List;

public interface BaseService {
    void populateCommonData(CommonFields requestData);

    CommonFields validateIdentifier(String entityName, String identifier);

    String saveSearchQuery(SavedQueryDto savedQueryDto, String source);

    List<SavedQueryDto> getSavedQuery(String source);
}
