package com.cheil.fileimport.strategies;

import com.cheil.core.mongo.dto.CommonWsDto;
import com.cheil.fileimport.service.impl.EntityAction;
import com.cheil.fileimport.service.impl.EntityField;
import com.cheil.qa.framework.ExtentManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FileImportFactory {

    private final Map<String, EntityAction> entityActionMap;

    public FileImportFactory(Map<String, EntityAction> entityActionMap) {
        this.entityActionMap = entityActionMap;
    }

    public EntityAction getModelService(String type) {
        EntityAction entityAction = entityActionMap.get(type);
        if (entityAction == null) {
            throw new RuntimeException("Unsupported model type");
        }
        return entityAction;
    }

    public void processRow(Map<String, EntityField> rowMap, String type, String repositoryName, String modelName, CommonWsDto commonWsDto) {
        EntityAction entityAction = getModelService(type);
        entityAction.processRow(rowMap, repositoryName, modelName, commonWsDto);
    }

    public String validate(List<String> header, String type) {
        EntityAction entityAction = getModelService(type);
        return entityAction.validate(header, type);
    }

    public ExtentManager getExtentManager(String type) {
        EntityAction entityAction = getModelService(type);
        return entityAction.getExtentManager();
    }
}
