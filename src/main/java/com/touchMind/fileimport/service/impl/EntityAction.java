package com.touchMind.fileimport.service.impl;

import com.touchMind.core.mongo.dto.CommonWsDto;
import com.touchMind.qa.framework.ExtentManager;

import java.util.List;
import java.util.Map;

public interface EntityAction<T> {
    void processRow(Map<String, EntityField> rowMap, String repositoryName, String modelName, CommonWsDto commonWsDto);

    String validate(List<String> header, String entityName);

    ExtentManager getExtentManager();
}
