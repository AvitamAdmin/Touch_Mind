package com.cheil.fileimport.service.impl;

import com.cheil.core.mongo.dto.CommonWsDto;
import com.cheil.qa.framework.ExtentManager;

import java.util.List;
import java.util.Map;

public interface EntityAction<T> {
    void processRow(Map<String, EntityField> rowMap, String repositoryName, String modelName, CommonWsDto commonWsDto);

    String validate(List<String> header, String entityName);

    ExtentManager getExtentManager();
}
