package com.touchMind.data.service.impl;

import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.generic.GenericRepository;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.data.BaseDataService;
import com.touchMind.data.service.DataService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonAccessoriesImpl extends BaseDataService implements DataService {
    Logger logger = LoggerFactory.getLogger(JsonAccessoriesImpl.class);

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public String getType() {
        return "JsonAccessoriesImpl";
    }

    @Override
    public boolean processApi(ReportDto reportDto, String api) {
        List<String> inputParams = reportDto.getDataSourceParams();
        if (inputParams == null || inputParams.isEmpty()) {
            logger.error("Missing required parameters hence ignoring further processing! ");
            return false;
        }
        if (StringUtils.isEmpty(reportDto.getRelationKeys())) {
            logger.error("No primary key found for entity hence ignoring the further processing");
            return false;
        }
        InputStream inputStream = getInputStreamForJson(api);
        DocumentContext doc = JsonPath.parse(inputStream);
        String relationsKeys = reportDto.getRelationKeys();
        String[] relationKeys = relationsKeys.split(",");
        for (String relationKey : relationKeys) {
            if (!TEM_VARIANT.equals(relationKey)) {
                processParams(doc, reportDto, relationKey);
            }
        }
        return true;
    }

    private void processParams(DocumentContext doc, ReportDto reportDto, String relationKey) {
        List<BaseEntity> documents = new ArrayList<>();
        if (StringUtils.isNotEmpty(relationKey) && relationKey.contains(" ")) {
            String[] expression = relationKey.split(" ");
            Object jsonData = doc.read(expression[0]);
            jsonData = doc.read(expression[1]);
            //TODO implement this part if needed
        } else {
            Object jsonData = doc.read(relationKey);
            if (jsonData instanceof JSONArray productsArray) {
                for (Object productObject : productsArray) {
                    Map<String, Object> productAndCategory = (LinkedHashMap) productObject;
                    JSONArray productsObjects = (JSONArray) productAndCategory.get("products");
                    Map<String, Object> category = (LinkedHashMap) productAndCategory.get("category");
                    for (Object productObj : productsObjects) {
                        Map<String, Object> document = new HashMap<>();
                        Map<String, Object> records = (LinkedHashMap) productObj;
                        for (Object nestedObject : records.keySet()) {
                            if (records.get(nestedObject) instanceof LinkedHashMap) {
                                document.putAll((LinkedHashMap) records.get(nestedObject));
                            } else if (records.get(nestedObject) instanceof String) {
                                document.put(String.valueOf(nestedObject), records.get(nestedObject));
                            }
                        }
                        document.putAll(category);
                        document.put(TEM_VARIANT, reportDto.getCurrentVariant());
                        document.put(TEM_SITE, reportDto.getCurrentSite());
                        //String key = reportDto.getCurrentSessionId()+"§"+reportDto.getCurrentVariant()+"§"+reportDto.getCurrentSite()+"$"+String.valueOf(model.get(TRADEINCODE)).replace(".","_");
                        BaseEntity newDoc = getNewDocument(reportDto.getCurrentNode(), reportDto.getCurrentSessionId(), reportDto.getCurrentVariant() + reportDto.getCurrentSite());
                        newDoc.setRecords(document);
                        documents.add(newDoc);
                    }
                }
            }
            GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
            genericRepository.saveAll(documents);
        }
    }
}
