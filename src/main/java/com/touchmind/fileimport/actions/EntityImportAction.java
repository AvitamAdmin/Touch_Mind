package com.touchmind.fileimport.actions;

import com.touchmind.core.mongo.dto.CommonWsDto;
import com.touchmind.core.mongo.model.CommonFields;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.service.CoreService;
import com.touchmind.core.service.RepositoryService;
import com.touchmind.fileimport.service.impl.EntityAction;
import com.touchmind.fileimport.service.impl.EntityField;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.fileimport.strategies.FieldProcessingFactory;
import com.touchmind.qa.framework.ExtentManager;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service(EntityType.ENTITY_IMPORT_ACTION)
public class EntityImportAction extends BaseAction implements EntityAction {

    public static final String MONGO_MODEL_BASE_PACKAGE = "com.cheil.core.mongo.model.";
    public static final String LONG = "Long";
    public static final String INTEGER = "Integer";
    public static final String DOUBLE = "Double";
    public static final String FLOAT = "Float";
    public static final String BOOLEAN = "Boolean";
    Logger logger = LoggerFactory.getLogger(EntityImportAction.class);
    @Autowired
    private CoreService coreService;
    @Autowired
    private FieldProcessingFactory fieldProcessingFactory;
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public String validate(List header, String entityName) {
        return super.initExtentReport(header, entityName);
    }

    private void setFieldValue(Object entity, String fieldName, EntityField entityField) {
        Field field = null;
        Object value = entityField.getAttributes() == null ? entityField.getValue() : fieldProcessingFactory.processField(entityField);
        try {
            field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            setValueForField(entity, field, value);
        } catch (Exception ex) {
            // Fields from super class set the values to super class field
            try {
                field = entity.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                setValueForField(entity, field, value);
            } catch (Exception e) {
                try {
                    field = entity.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    setValueForField(entity, field, value);
                } catch (Exception exc) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

    private void setValueForField(Object entity, Field field, Object value) throws IllegalAccessException {
        if (StringUtils.isEmpty(String.valueOf(value))) {
            return;
        }
        String fieldType = field.getType().getSimpleName();
        switch (fieldType) {
            case LONG:
                field.set(entity, NumberUtils.toLong(String.valueOf(value)));
                break;
            case INTEGER:
                field.set(entity, NumberUtils.toInt(String.valueOf(value)));
                break;
            case DOUBLE:
                field.set(entity, NumberUtils.toDouble(String.valueOf(value)));
                break;
            case FLOAT:
                field.set(entity, NumberUtils.toFloat(String.valueOf(value)));
                break;
            case BOOLEAN:
                field.set(entity, BooleanUtils.toBoolean(String.valueOf(value)));
                break;
            default:
                field.set(entity, value);
        }
    }

    @Override
    public void processRow(Map rowMap, String entityName, String modelName, CommonWsDto commonWsDto) {
        AtomicBoolean process = new AtomicBoolean(true);
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(entityName);
        Object entity = getEntity(rowMap, genericImportRepository, modelName);
        StringBuffer buffer = new StringBuffer();
        List<String> aClassFields = getCurrentEntityFields(entity);
        rowMap.keySet().stream().forEach(key -> {
            EntityField entityField = (EntityField) rowMap.get(key);
            if (String.valueOf(key).equalsIgnoreCase("identifier")) {
                if (StringUtils.isEmpty(entityField.getValue())) {
                    buffer.append("Identifier cannot be empty");
                    commonWsDto.setSuccess(false);
                    process.set(false);
                }
            }
            if (aClassFields.contains(key)) {
                setFieldValue(entity, (String) key, entityField);
                //buffer.append(entityField.getValue());
            } else {
                buffer.append("Failed Record: " + entityField.getValue());
            }
        });
        commonWsDto.setMessage(buffer.toString());
        commonWsDto.setSuccess(process.get());
        if (process.get()) {
            //extentTest.log(Status.INFO, buffer.toString() + " Imported !");
            CommonFields savedEntity = (CommonFields) genericImportRepository.save(entity);
            if (StringUtils.isEmpty(savedEntity.getRecordId())) {
                ObjectId objectId = savedEntity.getId();
                if (objectId != null) {
                    savedEntity.setRecordId(String.valueOf(objectId.getTimestamp()));
                    genericImportRepository.save(entity);
                }
            }
        }
    }

    private CommonFields getEntity(Map<String, EntityField> row, GenericImportRepository genericImportRepository, String modelName) {
        AtomicReference<String> field = new AtomicReference<>();
        AtomicReference<String> value = new AtomicReference<>();
        AtomicReference<String> pkType = new AtomicReference<>();
        row.keySet().stream().forEach(key -> {
            EntityField rowValue = row.get(key);
            Map<String, String> attributes = rowValue.getAttributes();
            if (attributes != null && attributes.containsKey("Pk")) {
                field.set(key);
                value.set(rowValue.getValue());
                pkType.set(attributes.get("Pk"));
            }
        });
        CommonFields commonFields = null;
        if (StringUtils.isNotEmpty(field.get()) && StringUtils.isNotEmpty(value.get()) && StringUtils.isNotEmpty(pkType.get())) {
            if ("identifier".equalsIgnoreCase(pkType.get())) {
                commonFields = genericImportRepository.findByIdentifierIgnoreCase(value.get());
            }
        }
        String creator = coreService.getCurrentUser().getUsername();
        if (commonFields == null) {
            commonFields = getEntityForRepository(modelName);
            commonFields.setStatus(true);
            commonFields.setCreationTime(new Date());
            commonFields.setCreator(creator);
        }
        commonFields.setLastModified(new Date());
        commonFields.setModifiedBy(creator);
        return commonFields;
    }

    @Override
    public ExtentManager getExtentManager() {
        return extentManager;
    }

    private CommonFields getEntityForRepository(String entity) {
        try {
            Class<?> myClass = Class.forName(MONGO_MODEL_BASE_PACKAGE + entity);
            Constructor<?> ctr = myClass.getConstructor();
            return (CommonFields) ctr.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
