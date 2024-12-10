package com.touchmind.data.service.impl;

import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import com.touchmind.data.BaseDataService;
import com.touchmind.data.service.DataService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonBaseDataServiceImpl extends BaseDataService implements DataService {

    public static final String MODELS = "models";
    public static final String TRADEINCODE = "tradeincode";
    public static final String COMPANY = "company";
    public static final String UPTOPRICE = "uptoprice";
    public static final String CASHBACK = "cashback";

    Logger logger = LoggerFactory.getLogger(JsonBaseDataServiceImpl.class);

    @Override
    public String getType() {
        return "JsonBaseDataServiceImpl";
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
            if (jsonData instanceof JSONArray result) {
                for (Object jsonRecord : result) {
                    Map<String, Object> record = (LinkedHashMap) jsonRecord;
                    Object ModelsObject = record.get(MODELS);
                    JSONArray models = (JSONArray) ModelsObject;
                    for (Object modelObject : models) {
                        LinkedHashMap model = (LinkedHashMap) modelObject;
                        //String key = reportDto.getCurrentSessionId()+"§"+reportDto.getCurrentVariant()+"§"+reportDto.getCurrentSite()+"$"+String.valueOf(model.get(TRADEINCODE)).replace(".","_");
                        Map<String, Object> document = new HashMap<>();
                        document.put(TEM_VARIANT, reportDto.getCurrentVariant());
                        document.put(TEM_SITE, reportDto.getCurrentSite());
                        document.put(COMPANY, String.valueOf(record.get(COMPANY)));
                        document.put(UPTOPRICE, String.valueOf(record.get(UPTOPRICE)));
                        document.put(CASHBACK, String.valueOf(record.get(CASHBACK)));
                        for (Object modelKey : model.keySet()) {
                            String modelRecord = String.valueOf(model.get(modelKey));
                            String finalKey = String.valueOf(modelKey).replace(".", "_");
                            document.put(finalKey, modelRecord);
                        }
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
