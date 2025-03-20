package com.touchMind.fileimport.service.impl;

import com.touchMind.core.mongo.model.CommonFields;
import com.touchMind.core.mongo.repository.generic.GenericImportRepository;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.fileimport.actions.BaseAction;
import com.touchMind.fileimport.service.FileExportService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    public String exportEntity(String entityName, Map<String, String> headerFields) throws IOException {
        GenericImportRepository genericImportRepository = repositoryService.getRepositoryForName(entityName);
        List<CommonFields> results = new ArrayList<>();
        results.addAll(genericImportRepository.findAll());
        String fileName = File.separator + entityName + "_" + df.format(new Date()) + "_impex.csv";
        if (CollectionUtils.isNotEmpty(results)) {
            Optional<CommonFields> exampleEntity = results.stream().findFirst();
            BufferedWriter writer = new BufferedWriter(new FileWriter(IMPEX_PATH + File.separator + fileName, true));
            Map<String, String> allHeaderFields = getCurrentEntityFieldsWithType(exampleEntity.get());
            Map<String, String> finalHeaderFields = new HashMap<>();
            if (MapUtils.isNotEmpty(headerFields)) {
                for (String key : headerFields.keySet()) {
                    finalHeaderFields.put(key, allHeaderFields.get(key));
                }
            } else {
                results = new ArrayList<>();
                results.add(exampleEntity.get());
                finalHeaderFields = allHeaderFields;
            }
            String impexHeader = getHeader(finalHeaderFields);
            writer.write(impexHeader);
            writer.newLine();
            Map<String, String> finalHeaderFields2 = finalHeaderFields;
            results.forEach(row -> {
                Class<?> clazz = row.getClass();
                List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
                List<Field> superClassFields = Arrays.asList(clazz.getSuperclass().getSuperclass().getDeclaredFields());
                Map<String, Object> valueMap = getValue(row, fields, superClassFields);
                StringBuffer dataBuffer = new StringBuffer();
                AtomicInteger counter = new AtomicInteger();
                Set<String> fieldKeys = finalHeaderFields2.keySet();
                Map<String, String> finalHeaderFields1 = finalHeaderFields2;
                fieldKeys.stream().forEach(fieldName -> {
                    Object object = valueMap.get(fieldName);
                    if (object != null) {
                        if (StringUtils.isNotEmpty(finalHeaderFields1.get(fieldName))) {
                            if (finalHeaderFields1.get(fieldName).contains(REF_IDENTIFIER))
                                populateDefaultData(dataBuffer, counter, fieldKeys, getValueForFieldName(row.getClass().getSuperclass().getSuperclass(), IDENTIFIER, row));
                            else if (finalHeaderFields1.get(fieldName).contains(REFS_IDENTIFIER)) {
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
            });
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
                    buffer.append(((CommonFields) entity).getIdentifier());
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
                        buffer.append(((CommonFields) entity).getIdentifier());
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
            if (counter.get() < headerFields.size())
                buffer.append(SEMICOLON);
        });
        return buffer.toString();
    }
}
