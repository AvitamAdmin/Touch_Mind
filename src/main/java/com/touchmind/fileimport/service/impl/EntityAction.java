package com.touchmind.fileimport.service.impl;

import com.touchmind.core.mongo.dto.CommonWsDto;
import com.touchmind.qa.framework.ExtentManager;

import java.util.List;
import java.util.Map;

public interface EntityAction<T> {
    void processRow(Map<String, EntityField> rowMap, String repositoryName, String modelName, CommonWsDto commonWsDto);

    String validate(List<String> header, String entityName);

    ExtentManager getExtentManager();
}
