package com.touchmind.fileimport.service.impl;

import com.touchmind.core.mongo.model.CommonFields;
import com.touchmind.core.mongo.repository.generic.GenericImportRepository;
import com.touchmind.core.service.RepositoryService;
import com.touchmind.fileimport.actions.BaseAction;
import com.touchmind.fileimport.service.FileExportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EntityExportServiceImpl extends BaseAction implements FileExportService {

    public static final String IMPEX_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
            + "resources" + File.separator + "static" + File.separator + "impex" + File.separator;
    public static final DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.GERMAN);
    public static final String REF_IDENTIFIER = "ref=identifier";
    public static final String REFS_IDENTIFIER = "refs=identifier";
    public static final String IDENTIFIER = "identifier";
    public static final String COMMA = "||";
    public static final String SEMICOLON = ",";
    private final Logger logger = LoggerFactory.getLogger(EntityExportServiceImpl.class);
    @Autowired
    private RepositoryService repositoryService;

    private static void populateDefaultData(StringBuffer dataBuffer, AtomicInteger counter, Set<String> fieldKeys, Object object) {
        dataBuffer.append(object);
        if (counter.get() < fieldKeys.size()) {
            dataBuffer.append(SEMICOLON);
        }
    }

    @Override
    public String exportEntity(String entityName) throws IOException {
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(entityName);
        List<CommonFields> result = genericImportRepository.findAll();
        Optional<CommonFields> exampleEntity = result.stream().findFirst();
        String fileName = File.separator + entityName + "_" + df.format(new Date()) + "_impex.csv";
        if (exampleEntity.isPresent()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(IMPEX_PATH + File.separator + fileName, true));
            Map<String, String> headerFields = getCurrentEntityFieldsWithType(exampleEntity.get());
            String impexHeader = getHeader(headerFields);
            writer.write(impexHeader);
            writer.newLine();
            CommonFields row = exampleEntity.get();
            Class<?> clazz = row.getClass();
            List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
            List<Field> superClassFields = Arrays.asList(clazz.getSuperclass().getSuperclass().getDeclaredFields());
            Map<String, Object> valueMap = getValue(row, fields, superClassFields);
            StringBuffer dataBuffer = new StringBuffer();
            AtomicInteger counter = new AtomicInteger();
            Set<String> fieldKeys = headerFields.keySet();
            fieldKeys.stream().forEach(fieldName -> {
                Object object = valueMap.get(fieldName);
                if (object != null) {
                    if (StringUtils.isNotEmpty(headerFields.get(fieldName))) {
                        if (headerFields.get(fieldName).contains(REF_IDENTIFIER))
                            populateDefaultData(dataBuffer, counter, fieldKeys, getValueForFieldName(row.getClass().getSuperclass().getSuperclass(), IDENTIFIER, row));
                        else if (headerFields.get(fieldName).contains(REFS_IDENTIFIER)) {
                            dataBuffer.append(getRefsForEntity(object));
                            if (counter.get() < fieldKeys.size()) {
                                dataBuffer.append(SEMICOLON);
                            }
                        } else {
                            populateDefaultData(dataBuffer, counter, fieldKeys, object);
                        }
                    } else {
                        populateDefaultData(dataBuffer, counter, fieldKeys, object);
                    }
                } else {
                    populateDefaultData(dataBuffer, counter, fieldKeys, object);
                }
                counter.getAndIncrement();
            });
            try {
                String data = dataBuffer.toString();
                writer.write(data.substring(0, data.length() - 1));
                writer.newLine();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            writer.flush();
            writer.close();
        }
        return fileName;
    }

    private String getRefsForEntity(Object objectList) {
        StringBuffer buffer = new StringBuffer();
        if (objectList instanceof List) {
            List<Object> entityList = (List<Object>) objectList;
            entityList.stream().forEach(entity -> {
                if (entity instanceof String) {
                    buffer.append(entity);
                } else if (entity instanceof CommonFields) {
                    buffer.append(((CommonFields) entity).getRecordId());
                }
                buffer.append(COMMA);
            });
        } else if (objectList instanceof Set) {
            Set<Object> entitySet = (Set<Object>) objectList;
            entitySet.stream().forEach(entity -> {
                if (entity != null) {
                    if (entity instanceof String) {
                        buffer.append(entity);
                    } else {
                        buffer.append(((CommonFields) entity).getRecordId());
                    }
                    buffer.append(COMMA);
                }
            });
        }
        return buffer.toString();
    }


    private Map<String, Object> getValue(Object row, List<Field> fields, List<Field> superClassFields) {
        Map<String, Object> rowMap = new HashMap<>();
        fields.stream().forEach(field -> {
            rowMap.put(field.getName(), getValue(field, row));
        });
        superClassFields.stream().forEach(field -> {
            rowMap.put(field.getName(), getValue(field, row));
        });
        return rowMap;
    }

    private Object getValueForFieldName(Class<?> clazz, String fieldName, Object row) {
        try {
            //Field field = clazz.getField(fieldName);
            Field[] fields = clazz.getDeclaredFields();
            Field field = Arrays.stream(fields).filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst().get();
            field.setAccessible(true);
            return field.get(row);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private Object getValue(Field field, Object row) {
        field.setAccessible(true);
        try {
            return field.get(row);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private String getHeader(Map<String, String> headerFields) {
        StringBuffer buffer = new StringBuffer();
        AtomicInteger counter = new AtomicInteger();
        headerFields.keySet().stream().forEach(key -> {
            buffer.append(headerFields.get(key) == null ? key : key + headerFields.get(key));
            counter.getAndIncrement();
            if (counter.get() < headerFields.keySet().size())
                buffer.append(SEMICOLON);
        });
        return buffer.toString();
    }
}
