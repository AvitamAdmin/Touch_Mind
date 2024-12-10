package com.cheil.fileimport.actions;

import com.aventstack.extentreports.ExtentTest;
import com.cheil.core.mongo.model.CommonBasicFields;
import com.cheil.qa.framework.ExtentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaseAction {

    public static final String IDENTIFIER_KEY = "identifier";
    private final String REF = ",ref=identifier)";
    private final String REFS = ",refs=identifier)";
    private final String TYPE = "(Type=";
    private final String IDENTIFIER = "(Pk=identifier)";
    @Autowired
    protected ExtentManager extentManager;
    protected ExtentTest extentTest;
    Logger logger = LoggerFactory.getLogger(BaseAction.class);
    String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    String EXTENT_HTML = ".html";

    protected String getReportFileName(String entityName) {
        String currentTime = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().getTime());
        return entityName + "_import_" + currentTime + EXTENT_HTML;
    }

    protected String initExtentReport(List header, String entityName) {
        String fileName = getReportFileName(entityName);
        extentManager.startNewReport(fileName);
        extentTest = extentManager.startNewTest(entityName, "initialised the report for " + entityName);
        return fileName;
    }

    public List<String> getCurrentEntityFields(Object entity) {
        List<String> aClassFields = new ArrayList<>();
        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(field -> {
            aClassFields.add(field.getName());
        });
        Arrays.stream(CommonBasicFields.class.getDeclaredFields()).forEach(field -> {
            aClassFields.add(field.getName());
        });
        return aClassFields;
    }

    public Map<String, String> getCurrentEntityFieldsWithType(Object entity) {
        Map<String, String> aClassFields = new HashMap<>();
        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(field -> {
            aClassFields.put(field.getName(), getAttributesForType(field));
        });
        Arrays.stream(CommonBasicFields.class.getDeclaredFields()).forEach(field -> {
            aClassFields.put(field.getName(), getAttributesForType(field));
        });
        return aClassFields;
    }

    private String getAttributesForType(Field field) {
        String attribute = null;
        if ("com.cheil.core.mongo.model".equals(field.getType().getPackage().getName())) {
            attribute = TYPE + field.getDeclaringClass().getSimpleName() + REF;
        } else if ("Set".equalsIgnoreCase(field.getType().getSimpleName()) || "List".equalsIgnoreCase(field.getType().getSimpleName())) {
            String typeName = field.getAnnotatedType().getType().getTypeName(); //java.util.Set<com.cheil.core.mongo.model.Role>
            try {
                String type = Class.forName(typeName.substring(typeName.indexOf("<") + 1, typeName.indexOf(">"))).getSimpleName();
                if (!type.equalsIgnoreCase("String")) {
                    attribute = TYPE + type + REFS;
                }
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
        } else if (IDENTIFIER_KEY.equals(field.getName())) {
            attribute = IDENTIFIER;
        }
        return attribute;
    }
}
