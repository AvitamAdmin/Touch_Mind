package com.touchMind.data;

import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.service.RepositoryService;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDataService {
    public static final String TEM_VARIANT = "temVariant";
    public static final String TEM_SITE = "temSite";
    public static final String $ROOT = "$.";
    @Autowired
    protected RepositoryService repositoryService;
    Logger logger = LoggerFactory.getLogger(BaseDataService.class);

    public InputStream getInputStreamForXml(String api) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(api);
        } catch (MalformedURLException e) {
            logger.error("URL Error", "API URL provided is incorrect");
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/xml");
            return connection.getInputStream();
        } catch (IOException e) {
            logger.error("URL Error", "API URL provided is incorrect" + e);
        }
        return null;
    }

    protected InputStream getInputStreamForJson(String api) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(api);
        } catch (MalformedURLException e) {
            logger.error("URL Error", "API URL provided is incorrect");
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            return connection.getInputStream();
        } catch (IOException e) {
            logger.error("URL Error", "API URL provided is incorrect" + e);
        }
        return null;
    }

    protected void processRelationKeys(ReportDto reportDto) {

    }

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
                        System.out.println(key + "#");
                        result.add(Map.of(key, map.get(key)));
                    }
                }
            }
        } else if (rootObject instanceof String) {
            System.out.println(rootObject);
            result.add(rootObject);
        }
    }

    public String[] getParts(String param) {
        if (StringUtils.isNotEmpty(param)) {
            int firstIndex = param.indexOf(" ");
            return new String[]{param.substring(0, firstIndex), param.substring(firstIndex + 1)};
        }
        return null;
    }

    protected BaseEntity getNewDocument(String nodeId, String sessionId, String recordId) {
        BaseEntity baseEntity;
        baseEntity = repositoryService.getEntityForNodeId(nodeId);
        new ObjectId();
        baseEntity.setId(ObjectId.get());
        baseEntity.setIdentifier(recordId);
        baseEntity.setSessionId(sessionId);
        return baseEntity;
    }
}
