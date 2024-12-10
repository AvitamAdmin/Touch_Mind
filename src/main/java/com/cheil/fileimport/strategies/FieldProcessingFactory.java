package com.cheil.fileimport.strategies;

import com.cheil.fileimport.actions.FieldAction;
import com.cheil.fileimport.service.impl.EntityField;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FieldProcessingFactory {
    private final Map<String, FieldAction> fieldActionMap;

    public FieldProcessingFactory(Map<String, FieldAction> fieldActionMap) {
        this.fieldActionMap = fieldActionMap;
    }

    public FieldAction getFieldService(String type) {
        return fieldActionMap.get(type);
    }

    public Object processField(EntityField entityField) {
        if (entityField.getAttributes() == null) {
            return entityField.getValue();
        }
        Object fieldValue = null;
        Map<String, String> attributes = entityField.getAttributes();
        if (attributes.containsKey("ref")) {
            FieldAction fieldAction = getFieldService(FieldType.ONE_TO_ONE_REF_FIELD);
            fieldValue = fieldAction.performFieldAction(entityField);
        }
        if (attributes.containsKey("refs")) {
            FieldAction fieldAction = getFieldService(FieldType.ONE_TO_MANY_REF_FIELD);
            fieldValue = fieldAction.performFieldAction(entityField);
        }
        if (attributes.containsKey("Pk")) {
            fieldValue = entityField.getValue();
        }
        return fieldValue;
    }
}
