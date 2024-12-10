package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import com.cheil.core.service.JsonService;
import com.cheil.core.service.RepositoryService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JsonServiceImpl extends ApiCommonService implements JsonService {

    public static final String TEM_VARIANTS = "temVariants";
    public static final String $ROOT = "$.";
    private final Logger logger = LoggerFactory.getLogger(JsonServiceImpl.class);
    @Autowired
    private RepositoryService repositoryService;

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream(new File("trade-in.json"));
        Map<String, Object> params = new HashMap<>();
        ReportDto reportDto = new ReportDto();
        //reportDto.setDataSourceParams(List.of("[*]_products _stock_stockLevelStatus"));
        //reportDto.setDataSourceParams(List.of("[*]_products _modelcode"));
        //reportDto.setDataSourceParams(List.of("[*]_products _price_value"));
        reportDto.setDataSourceParams(List.of("_companies _company", "[*]models tradeincode"));
        reportDto.setRelationKeys("temVarient,[*]models tradeincode");
        //reportDto.setRelationKeys("temVarient,temVarient");
        JsonServiceImpl jsonService = new JsonServiceImpl();
        DocumentContext doc = JsonPath.parse(is);
        Map<Object, Map<String, Object>> result = jsonService.processJson(doc, reportDto);
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i));
            System.out.println("---------------------------");
        }
    }

    private boolean getDataForComplexParam(DocumentContext doc, ReportDto reportDto, String relationKey, String param, Map<Object, Map<String, Object>> documents) {
        String recordId = reportDto.getCurrentVariant();
        String[] paramExpression = getParts(param.replaceAll("\\_", "."));
        Object rootObject = null;
        try {
            rootObject = doc.read($ROOT + paramExpression[0]);
        } catch (PathNotFoundException e) {
            logger.error("Could not process param " + paramExpression[0] + " PathNotFoundException");
            return false;
        }
        if (rootObject instanceof JSONArray rootArray) {
            for (int i = 0; i < rootArray.size(); i++) {
                try {
                    if (rootArray.get(i) instanceof JSONArray levelOneArray) {
                        for (int j = 0; j < levelOneArray.size(); j++) {
                            String relationValue = getRelationValueForKey(levelOneArray.get(j), relationKey);
                            if (levelOneArray.get(j) instanceof JSONArray levelTwooArray) {
                                String seperator = "";
                                StringBuilder buffer = new StringBuilder();
                                for (Object obj : levelTwooArray) {
                                    if (obj instanceof LinkedHashMap) {
                                        Object value = JsonPath.read(obj, $ROOT + paramExpression[1]);
                                        buffer.append(seperator).append(value);
                                    }
                                    if (obj instanceof String) {
                                        buffer.append(seperator).append(obj);
                                    }
                                    seperator = ",";
                                }
                                processField(recordId + "§" + relationValue + "|" + param, j, buffer.toString(), documents);
                                processField(recordId + "§" + relationValue + "|" + TEM_SITE, j, reportDto.getCurrentSite(), documents);
                            }
                            if (levelOneArray.get(j) instanceof LinkedHashMap) {
                                Object arrayValue = JsonPath.read(levelOneArray.get(j), $ROOT + paramExpression[1]);
                                Object value = null;
                                if (arrayValue instanceof JSONArray) {
                                    if (((JSONArray) arrayValue).size() > 0) {
                                        value = ((JSONArray) arrayValue).get(0);
                                    }
                                }
                                if (arrayValue instanceof String) {
                                    value = arrayValue;
                                }
                                processField(recordId + "§" + relationValue + "|" + param, j, value, documents);
                                processField(recordId + "§" + relationValue + "|" + TEM_SITE, j, reportDto.getCurrentSite(), documents);
                            }
                            if (levelOneArray.get(j) instanceof String) {
                                processField(recordId + "§" + relationValue + "|" + param, j, levelOneArray.get(j), documents);
                                processField(recordId + "§" + relationValue + "|" + TEM_SITE, j, reportDto.getCurrentSite(), documents);
                            }
                        }
                    }
                    if (rootArray.get(i) instanceof LinkedHashMap) {
                        Object arrayValue = JsonPath.read(rootArray.get(i), $ROOT + paramExpression[1]);
                        Object value = null;
                        if (arrayValue instanceof JSONArray) {
                            if (((JSONArray) arrayValue).size() == 0) {
                                String[] expressions = paramExpression[1].split("[.]");
                                if (expressions.length >= 4) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append($ROOT);
                                    int maxCount = expressions.length - 1;
                                    for (int c = 1; c < maxCount; c++) {
                                        builder.append(expressions[c]).append("[*].");
                                    }
                                    builder.append(expressions[maxCount]);
                                    arrayValue = JsonPath.read(rootArray.get(i), builder.toString());
                                }
                            }
                            if (reportDto.getMapping().equalsIgnoreCase("aemSearchAPIMapping") || param.contains("categories[*]")) {
                                int count = 0;
                                for (Object val : ((JSONArray) arrayValue)) {
                                    processField(param, count++, val, documents);
                                    processField(TEM_SITE, count, reportDto.getCurrentSite(), documents);
                                }
                                return true;
                            }
                            value = ((JSONArray) arrayValue).size() > 0 ? ((JSONArray) arrayValue).get(0) : null;
                        }
                        if (arrayValue instanceof String) {
                            value = arrayValue;
                        }
                        processField(param, i, value, documents);
                        processField(TEM_SITE, i, reportDto.getCurrentSite(), documents);
                    }
                    if (rootArray.get(i) instanceof String) {
                        processField(param, i, rootArray.get(i), documents);
                    }
                } catch (PathNotFoundException e) {
                    logger.error("Could not process param " + paramExpression[1] + " PathNotFoundException");
                    return false;
                }
            }
        } else {
            processField(param, 0, rootObject, documents);
            processField(TEM_SITE, 0, reportDto.getCurrentSite(), documents);
        }
        return true;
    }

    private String getRelationValueForKey(Object doc, String relationnKey) {
        String[] expression = getParts(relationnKey);
        try {
            if (null != expression) {
                return JsonPath.read(doc, expression[1]);
            }
        } catch (PathNotFoundException e) {
            logger.error("No relation path found for key " + expression[1]);
        }
        return null;
    }

    private void processField(String param, Integer index, Object fieldValue, Map<Object, Map<String, Object>> documents) {
        Map<String, Object> newDoc = new HashMap<>();
        newDoc.put(param, fieldValue);
        mergeDocument(documents, index, newDoc);
    }

    protected boolean getDataForSimpleParams(DocumentContext doc, ReportDto reportDto, String param, Map<Object, Map<String, Object>> documents) {
        String expression = param.replaceAll("\\_", ".");
        Object object = null;
        try {
            object = doc.read($ROOT + expression);
        } catch (PathNotFoundException e) {
            logger.error("Could not process param " + param + " PathNotFoundException");
            return false;
        }
        Map<String, Object> newDoc = null;
        if (object instanceof JSONArray array) {
            /*String[] expressions = expression.split("\\.");
            boolean splCase = false;
            if (array.isEmpty() && doc.json().toString().contains(expressions[1])) {
                splCase = true;
            }
            if (splCase) {
                if (expressions.length > 3) {
                    for (int i = 0; i < array.size(); i++) {
                        Object innerObject = doc.read($ROOT + expressions[0] + expressions[1] + "[" + i + "]" + expressions[2]);
                        JSONArray innerArray = (JSONArray) innerObject;
                        for (int j = 0; j < innerArray.size(); j++) {
                            newDoc = new HashMap<>();
                            newDoc.put(param, innerArray.get(j));
                            newDoc.put(TEM_SITE, reportDto.getCurrentSite());
                            mergeDocument(documents, j, newDoc);
                        }
                    }
                } else {
                    for (int i = 0; i < array.size(); i++) {
                        Object innerObject = doc.read($ROOT + expressions[0] + expressions[1] + "[" + i + "]" + expressions[2]);
                        JSONArray innerArray = (JSONArray) innerObject;
                        newDoc = new HashMap<>();
                        newDoc.put(param, innerArray.get(i));
                        newDoc.put(TEM_SITE, reportDto.getCurrentSite());
                        mergeDocument(documents, i, newDoc);
                    }
                }
            }*/
            for (int i = 0; i < array.size(); i++) {
                newDoc = new HashMap<>();
                newDoc.put(param, array.get(i));
                newDoc.put(TEM_SITE, reportDto.getCurrentSite());
                mergeDocument(documents, i, newDoc);
            }
        } else {
            newDoc = new HashMap<>();
            newDoc.put(param, object);
            newDoc.put(TEM_SITE, reportDto.getCurrentSite());
            mergeDocument(documents, 0, newDoc);
        }
        return true;
    }

    @Override
    public boolean processJsonData(InputStream inputStream, ReportDto reportDto) {
        if (StringUtils.isEmpty(reportDto.getRelationKeys())) {
            logger.error("No primary key found for entity hence ignoring the further processing");
            return false;
        }
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
        DocumentContext doc = JsonPath.parse(inputStream);
        Map<Object, Map<String, Object>> result = processJson(doc, reportDto);
        return saveDocuments(result, genericRepository, reportDto);
    }

    @Override
    public boolean processJsonData(String json, ReportDto reportDto) {
        if (StringUtils.isEmpty(reportDto.getRelationKeys())) {
            logger.error("No primary key found for entity hence ignoring the further processing");
            return false;
        }
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
        DocumentContext doc = JsonPath.parse(json);
        Map<Object, Map<String, Object>> result = processJson(doc, reportDto);
        return saveDocuments(result, genericRepository, reportDto);
    }

    private Map<Object, Map<String, Object>> processJson(DocumentContext doc, ReportDto reportDto) {
        List<String> inputParams = reportDto.getDataSourceParams();
        if (inputParams == null || inputParams.isEmpty()) {
            logger.error("Missing required parameters hence ignoring further processing! ");
            return null;
        }
        Map<Object, Map<String, Object>> documents = new HashMap<>();
        String relationKey = null;
        for (String param : reportDto.getRelationKeys().split(",")) {
            if (TEM_SITE.equals(param) || TEM_VARIANTS.equals(param) || TEM_VARIANT.equals(param)) {
                continue;
            }
            relationKey = param;
            reportDto.setCurrentRelationKey(param);
            if (param.contains(" ")) {
                getDataForComplexParam(doc, reportDto, relationKey, param, documents);
            } else {
                getDataForSimpleParams(doc, reportDto, param, documents);
            }
        }
        for (String param : inputParams) {
            if ((relationKey != null && relationKey.equals(param)) || TEM_SITE.equals(param) || TEM_VARIANTS.equals(param) || TEM_VARIANT.equals(param)) {
                continue;
            }
            if (param.contains(" ")) {
                getDataForComplexParam(doc, reportDto, relationKey, param, documents);
            } else {
                getDataForSimpleParams(doc, reportDto, param, documents);
            }
        }
        return documents;
    }
/*
    public static void main(String[] args) throws FileNotFoundException {
        InputStream is = new FileInputStream(new File("jsonExample.json"));
        DocumentContext doc = JsonPath.parse(is);
        //Object rootObject =  doc.read($ROOT +".companies..models");
        List<Object> result = new ArrayList<>();
        String[] params ="[*].products .modelcode".split(" ");
        Object rootObject = doc.read($ROOT + params[0]);
        JsonServiceImpl srcImpl = new JsonServiceImpl();
        Object leaf = JsonPath.read(rootObject,params[1]);
        boolean isKey=false;
        boolean isArray=false;
        Integer counter = 0;
        srcImpl.printJson(leaf,result,params[1],"1","2",isKey,isArray,counter);
        System.out.println(result);
    }

 */

    public void printJson(Object rootObject, List<Object> result, String param, String recordId, String relationKey, boolean isChildValueAsKey, boolean isChildArray, Integer counter) {
        counter++;
        if (rootObject instanceof JSONArray josonArray) {
            for (int i = 0; i < josonArray.size(); i++) {
                printJson(josonArray.get(i), result, param, recordId, relationKey, isChildValueAsKey, isChildArray, counter);
            }
        } else if (rootObject instanceof LinkedHashMap) {
            Map map = (LinkedHashMap) rootObject;
            for (Object key : map.keySet()) {
                if (map.get(key) instanceof JSONArray) {
                    printJson(map.get(key), result, param, recordId, relationKey, isChildValueAsKey, isChildArray, counter);
                } else {
                    if (map.get(key) != null) {
                        result.add(Map.of(key, map.get(key)));
                    }
                }
            }
        } else if (rootObject instanceof String) {
            result.add(rootObject);
        }
    }


}
