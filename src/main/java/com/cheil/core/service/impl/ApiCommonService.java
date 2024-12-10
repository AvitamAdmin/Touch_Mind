package com.cheil.core.service.impl;

import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.model.DataRelationParams;
import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import com.cheil.core.mongo.repository.DataRelationParamsRepository;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import com.cheil.core.service.RepositoryService;
import com.cheil.core.service.ServiceUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ApiCommonService {

    public static final String HTTP = "http";
    public static final String TEM_VARIANT = "temVariant";
    public static final String TEM_VARIANTS = "temVariants";
    public static final String TEM_SITE = "temSite";
    public static final String TEM_IMEI = "temIMEI";
    Logger logger = LoggerFactory.getLogger(ApiCommonService.class);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private DataRelationParamsRepository dataRelationParamsRepository;

    private static void checkForSpecialCaseLooping(List<String> primaryKeys, Map<String, String> primaryRecords, Map<String, Object> finalRecords, Map.Entry<String, Object> mapData) {
        String value = String.valueOf(mapData.getValue());
        if (value.contains(",") && mapData.getKey().contains("product§aemAdditionalInfoTypes§aemAdditionalInfos")) {
            primaryKeys.add(mapData.getKey());
            int count = 0;
            for (String val : value.split(",")) {
                primaryRecords.put(mapData.getKey() + count, val);
                count++;
            }
        } else {
            finalRecords.put(mapData.getKey(), value);
        }
    }

    protected void saveDocument(GenericRepository genericRepository, Integer indexId, Map<String, Object> record, ReportDto reportDto) {
        List<BaseEntity> entities = null;
        StringBuffer buffer = new StringBuffer();
        record.put(TEM_SITE, reportDto.getCurrentSite());
        boolean updateRequired = true;

        String recordId = null;
        for (String key : reportDto.getRelationKeys().split(",")) {
            if (!TEM_VARIANT.equals(key) && record.containsKey(key.trim())) {
                buffer.append(record.get(key.trim()));
            }
            if (TEM_VARIANT.equals(key)) {
                recordId = StringUtils.isNotEmpty(reportDto.getCurrentVariant()) ? reportDto.getCurrentVariant() : reportDto.getSkus();
                record.put(TEM_VARIANT, recordId + "|" + reportDto.getSkuUrl());
            }
            if (TEM_VARIANTS.equals(key)) {
                record.put(TEM_VARIANT, reportDto.getCurrentVariant());
                recordId = reportDto.getCurrentVariant();
            }
            if (TEM_IMEI.equals(key)) {
                record.put(TEM_IMEI, reportDto.getTemImei());
            }
        }
        String relationId = buffer.toString();

        recordId = recordId == null ? relationId : recordId;

        if (StringUtils.isNotEmpty(recordId)) {
            if (reportDto.getCurrentNode() != null) {
                if (indexId != null) {
                    if (StringUtils.isNotEmpty(relationId)) {
                        entities = genericRepository.findByIndexIdAndRecordIdAndRelationIdAndSessionId(indexId, recordId + reportDto.getCurrentSite(), relationId, reportDto.getCurrentSessionId());
                    } else {
                        entities = genericRepository.findByIndexIdAndRecordIdAndSessionId(indexId, recordId + reportDto.getCurrentSite(), reportDto.getCurrentSessionId());
                    }
                } else {
                    if (StringUtils.isNotEmpty(relationId)) {
                        entities = genericRepository.findByRecordIdAndRelationIdAndSessionId(recordId + reportDto.getCurrentSite(), relationId, reportDto.getCurrentSessionId());
                    } else {
                        entities = genericRepository.findByRecordIdAndSessionId(recordId + reportDto.getCurrentSite(), reportDto.getCurrentSessionId());
                    }
                }

            } else {
                //TODO check this if still saving correctly
                entities = genericRepository.findByRecordId(recordId);
                for (BaseEntity entity : entities) {
                    entity = getNewDocument(reportDto.getCurrentNode(), reportDto.getCurrentSessionId(), recordId + reportDto.getCurrentSite(), relationId, indexId);
                    entity.setRecords(entity.getRecords());
                }
            }
            if (entities != null && !entities.isEmpty()) {
                record.put(TEM_SITE, reportDto.getCurrentSite());
                boolean isDeleted = false;
                for (BaseEntity entity : entities) {
                    List<String> primaryKeys = new ArrayList<>();
                    Map<String, String> primaryRecords = new HashMap<>();
                    Map<String, Object> finalRecords = new HashMap<>();
                    for (Map.Entry<String, Object> mapData : record.entrySet()) {
                        checkForSpecialCaseLooping(primaryKeys, primaryRecords, finalRecords, mapData);
                    }
                    if (CollectionUtils.isNotEmpty(primaryKeys)) {
                        if (!isDeleted) {
                            isDeleted = true;
                            genericRepository.deleteAllBySessionId(reportDto.getCurrentSessionId());
                        }
                        updateRequired = populatePrimaryKeyData(genericRepository, indexId, reportDto, updateRequired, recordId, relationId, primaryKeys, primaryRecords, finalRecords);
                    } else {
                        entity.getRecords().putAll(record);
                    }
                }
            } else {
                BaseEntity baseEntitySave = getNewDocument(reportDto.getCurrentNode(), reportDto.getCurrentSessionId(), recordId + reportDto.getCurrentSite(), relationId, indexId);
                record.put(TEM_SITE, reportDto.getCurrentSite());
                baseEntitySave.setRecords(record);
                genericRepository.save(baseEntitySave);
            }
            if (updateRequired) {
                genericRepository.saveAll(entities);
            }
            logger.info("Document was saved in repository " + entities.size());
        }
        logger.info("No Data to Save");
    }

    private boolean populatePrimaryKeyData(GenericRepository genericRepository, Integer indexId, ReportDto reportDto, boolean updateRequired, String recordId, String relationId, List<String> primaryKeys, Map<String, String> primaryRecords, Map<String, Object> finalRecords) {
        int index = 0;
        for (Map.Entry<String, String> mapData : primaryRecords.entrySet()) {
            boolean process = false;
            for (String key : primaryKeys) {
                String val = primaryRecords.get(key + index);
                if (StringUtils.isNotEmpty(val)) {
                    process = true;
                    finalRecords.put(key, val);
                }
            }
            if (process) {
                updateRequired = false;
                BaseEntity baseEntitySave = getNewDocument(reportDto.getCurrentNode(), reportDto.getCurrentSessionId(), recordId + reportDto.getCurrentSite(), relationId, indexId);
                baseEntitySave.setRecords(finalRecords);
                genericRepository.save(baseEntitySave);
            }
            index++;
        }
        return updateRequired;
    }

    protected boolean saveDocuments(Map<Object, Map<String, Object>> records, GenericRepository genericRepository, ReportDto reportDto) {
        if (records.size() > 1) {
            Integer index = 0;
            for (Object key : records.keySet()) {
                saveDocument(genericRepository, index, records.get(key), reportDto);
                index++;
            }
        } else {
            for (Object key : records.keySet()) {
                saveDocument(genericRepository, null, records.get(key), reportDto);
            }
        }
        return true;
    }

    protected BaseEntity getNewDocument(String nodeId, String sessionId, String recordId, String relationId, Integer index) {
        BaseEntity baseEntity;
        baseEntity = repositoryService.getEntityForNodeId(nodeId);
        baseEntity.setId(new ObjectId().get());
        baseEntity.setRecordId(recordId);
        baseEntity.setRelationId(relationId);
        baseEntity.setSessionId(sessionId);
        if (index != null) {
            baseEntity.setIndexId(String.valueOf(index));
        }
        return baseEntity;
    }

    public void mergeDocument(Map<Object, Map<String, Object>> documents, Object currentIndex, Map<String, Object> newDocument) {
        Map<String, Object> oldDocument = null;
        if (documents.containsKey(currentIndex)) {
            oldDocument = documents.get(currentIndex);
            oldDocument.putAll(newDocument);
        } else {
            oldDocument = newDocument;
        }
        documents.put(currentIndex, oldDocument);
    }

    public String[] getParts(String param) {
        if (StringUtils.isNotEmpty(param)) {
            int firstIndex = param.indexOf(" ");
            if (firstIndex > 0) {
                return new String[]{param.substring(0, firstIndex), param.substring(firstIndex + 1)};
            }
        }
        return null;
    }

    public Map<String, String> getRelationKeysForDatasource(DataSource dataSource, String node) {
        Map<String, String> relationKeys = new HashMap<>();
        List<DataRelationParams> dataRelations = dataRelationParamsRepository.findAll();
        Optional<DataRelationParams> dataRelationParamsOptional = dataRelations.stream().filter(dataRelationParams -> (dataRelationParams.getDataSource() != null &&
                dataRelationParams.getDataSource().equals(dataSource.getId()))).findFirst();
        if (dataRelationParamsOptional.isPresent()) {
            DataRelationParams dataRelationParam = dataRelationParamsOptional.get();
            relationKeys.put(ServiceUtil.getDocumentRepoForReportId(node), dataRelationParam.getSourceKeyOne());
        }
        return relationKeys;
    }
}
