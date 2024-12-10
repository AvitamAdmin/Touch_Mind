package com.touchmind.fileimport.strategies;

import com.touchmind.fileimport.service.EntityService;

import java.util.Map;

public class EntityFactory {
    private Map<String, EntityService> entityServiceMap;

    public EntityService getModelService(String type) {
        EntityService entityService = entityServiceMap.get(type);
        if (entityService == null) {
            throw new RuntimeException("Unsupported entity type");
        }
        return entityService;
    }

    public Object getEntity(String type) {
        EntityService entityService = getModelService(type);
        return entityService.performEntityAction();
    }

}
