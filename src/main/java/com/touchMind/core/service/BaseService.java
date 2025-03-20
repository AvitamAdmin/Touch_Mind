package com.touchMind.core.service;

import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.model.CommonFields;

import java.util.List;

public interface BaseService {
    void populateCommonData(CommonFields requestData);

    CommonFields validateIdentifier(String entityName, String identifier);

    String saveSearchQuery(SavedQueryDto savedQueryDto, String source);

    List<SavedQueryDto> getSavedQuery(String source);
}
