package com.touchmind.tookit.service.impl;

import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.PriceDocuments;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.model.SourceTargetParamMapping;
import com.touchmind.core.mongo.model.Subsidiary;
import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.NodeRepository;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import com.touchmind.core.service.RepositoryService;
import com.touchmind.mail.service.MailService;
import com.touchmind.tookit.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportServiceImpl extends BaseService implements ReportService {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
    public static final String P_NAMES = "pNames";
    public static final String P_VALUES = "pValues";
    public static final String TOOLKIT = "/toolkit/";
    public static final String PRODUCT_DISCOUNT_VALUE = "product§discount§value";
    @Autowired
    protected NodeRepository nodeRepository;
    Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    MailService mailService;
    @Autowired
    Environment env;
    @Autowired
    SubsidiaryRepository subsidiaryRepository;
    NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;

    @Override
    public List<BaseEntity> getReport(ReportDto reportDto) {
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
        if (reportDto.getEnableHistory() == null || !reportDto.getEnableHistory()) {
            genericRepository.deleteAllBySessionId(reportDto.getCurrentSessionId());
        }
        processData(reportDto);
        return genericRepository.getBySessionId(reportDto.getCurrentSessionId());
    }

    @Override
    public String getCurrentUserSessionId(HttpServletRequest request, String checkType) {
        String checkTypeWithoutSpaces = StringUtils.deleteWhitespace(checkType);
        HttpSession session = request.getSession();
        String mainSession = (String) session.getAttribute("currentUserSession");

        if (StringUtils.isNotEmpty(mainSession)) {
            String currentCheckTypeSession = (String) session.getAttribute(mainSession + checkTypeWithoutSpaces);
            if (StringUtils.isNotEmpty(currentCheckTypeSession)) {
                return currentCheckTypeSession;
            } else {
                currentCheckTypeSession = mainSession + checkTypeWithoutSpaces;
                session.setAttribute(currentCheckTypeSession, currentCheckTypeSession);
                return currentCheckTypeSession;
            }
        } else {
            UUID uuid = UUID.randomUUID();
            String currentCheckTypeSession = uuid + checkTypeWithoutSpaces;
            session.setAttribute("currentUserSession", uuid.toString());
            session.setAttribute(currentCheckTypeSession, currentCheckTypeSession);
            return currentCheckTypeSession;
        }
    }

    @Override
    public Map<String, List<String>> getHeaders(Node node, String subsidiary, String mapping) {
        List<SourceTargetMapping> sourceTargetMappingList = sourceTargetMappingRepository.findBySubsidiariesAndNode(subsidiary, String.valueOf(node.getId()));
        if (CollectionUtils.isNotEmpty(sourceTargetMappingList)) {
            List<String> headers = new ArrayList<>();
            List<String> params = new ArrayList<>();

            if (sourceTargetMappingList.size() == 1) {
                for (SourceTargetParamMapping sourceTargetParamMapping : sourceTargetMappingList.get(0).getSourceTargetParamMappings()) {
                    headers.add(sourceTargetParamMapping.getHeader());
                    params.add(sourceTargetParamMapping.getParam());
                }
            } else {
                for (SourceTargetMapping sourceTargetMapping : sourceTargetMappingList) {
                    if (sourceTargetMapping.getIdentifier().contains(mapping)) {
                        for (SourceTargetParamMapping sourceTargetParamMapping : sourceTargetMapping.getSourceTargetParamMappings()) {
                            headers.add(sourceTargetParamMapping.getHeader());
                            params.add(sourceTargetParamMapping.getParam());
                        }
                    }
                }
            }
            Map<String, List<String>> headersAndParams = new HashMap<>();
            headersAndParams.put(P_NAMES, headers);
            headersAndParams.put(P_VALUES, params);
            return headersAndParams;
        }
        return null;
    }

    @Override
    public void processData(Map<String, String> reportData) {
        ReportDto reportDto = getReportDto(reportData);
        List<BaseEntity> documents;
        String jobTime = df.format(new Date());
        saveCronHistory(reportData.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, jobTime, "Running", null, reportDto.getCurrentSessionId());
        try {
            documents = getReport(reportDto);
        } catch (Exception e) {
            logger.error(e.getMessage() + e);
            saveCronHistory(reportData.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, df.format(new Date()), "Failed", e.getMessage(), reportDto.getCurrentSessionId());
            return;
        }
        if (CollectionUtils.isNotEmpty(documents)) {

            List<String> headers = new ArrayList<>();
            List<String> params = new ArrayList<>();
            Map<String, String> pivotHeaders = new HashMap<>();
            List<Map<String, Object>> data = new ArrayList<>();

            BaseEntity entity = documents.get(0);
            int processedSkuCount = 0;
            if (entity instanceof PriceDocuments && reportDto.getCurrentSessionId().contains("%_")) {
                String[] parts = reportDto.getCurrentSessionId().split("%_");
                if (parts.length > 0) {
                    double discount = Double.valueOf(parts[0]);
                    List<BaseEntity> priceDocuments = new ArrayList<>();
                    for (BaseEntity document : documents) {
                        Map<String, Object> records = document.getRecords();
                        if (records.containsKey(PRODUCT_DISCOUNT_VALUE)) {
                            try {
                                Number discountAsString = format.parse(String.valueOf(records.get(PRODUCT_DISCOUNT_VALUE)).replaceAll("%", ""));
                                if (discountAsString.doubleValue() >= discount) {
                                    priceDocuments.add(document);
                                }
                            } catch (ParseException pe) {
                                LOG.error(pe.getMessage());
                            }
                        }
                    }
                    data = populateData(priceDocuments, headers, params, pivotHeaders, reportDto.getMapping(), reportDto.getSubsidiary(), reportDto.getCurrentNode());
                    processedSkuCount = priceDocuments.size();
                    logger.info("Processed : " + priceDocuments.size());
                }
            } else {
                data = populateData(documents, headers, params, pivotHeaders, reportDto.getMapping(), reportDto.getSubsidiary(), reportDto.getCurrentNode());
                processedSkuCount = documents.size();
                logger.info("Processed : " + documents.size());
            }
            if (StringUtils.isNotEmpty(reportDto.getEmail())) {
                mailService.sendToolkitEmail(reportDto, data, headers, params, pivotHeaders);
            }
            saveCronHistory(reportData.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), processedSkuCount, df.format(new Date()), "Completed", null, reportDto.getCurrentSessionId());
        } else {
            saveCronHistory(reportData.get("sessionId"), reportDto.getSubsidiary().getIdentifier(), reportDto.getEmail(), 0, df.format(new Date()), "No Data Processed", null, reportDto.getCurrentSessionId());
        }
    }

    private List<Map<String, Object>> populateData(List<BaseEntity> documents, List<String> headers, List<String> params, Map<String, String> pivotHeaders, String mapping, Subsidiary subsidiary, String currentNode) {
        //TODO check if the record is correctly fetched
        Node node = nodeRepository.findByRecordId(currentNode);
        if (node != null) {
            getHeaders(node, String.valueOf(subsidiary.getRecordId()), headers, params, mapping, pivotHeaders);
        }
        return adjustReportForHeaders(documents, params);
    }

    @Override
    public List<Map<String, Object>> adjustReportForHeaders(List<BaseEntity> documents, List<String> pValues) {
        if (documents == null) {
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (BaseEntity baseEntity : documents) {
            if (isDocument(baseEntity.getRecords())) {
                result.add(getDocumentForPValues(baseEntity.getRecords(), pValues));
            } else {
                Map<String, Object> document = baseEntity.getRecords();
                result.addAll(getRecordWithCorrectKeys(getReport(document)));
            }
        }
        return result;
    }

    @Override
    public boolean isDocument(Map<String, Object> document) {
        for (String key : document.keySet()) {
            if (key.contains("|")) {
                return false;
            }
        }
        return true;
    }

    protected Map<String, Object> getDocumentForPValues(Map<String, Object> document, List<String> pValues) {
        Map<String, Object> result = new HashMap<>();
        for (String key : document.keySet()) {
            for (String pValue : pValues) {
                if (key.endsWith(pValue)) {
                    result.put(pValue, document.get(key));
                }
            }
        }
        return result;
    }

    protected List<Map<String, Object>> getRecordWithCorrectKeys(Map<String, Map<String, Object>> records) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : records.keySet()) {
            Map<String, Object> record = records.get(key);
            Map<String, Object> temp = new HashMap<>();
            for (String field : record.keySet()) {
                if (field.contains("|") && !field.contains("g|")) {
                    temp.put(TEM_VARIANT, field.substring(0, field.indexOf("§")));
                }
                temp.put((field.contains("|") && !field.contains("g|")) ? field.substring(field.indexOf("|") + 1) : field, record.get(field));
            }
            temp.put(TEM_SITE, record.get(TEM_SITE));
            result.add(temp);
        }
        return result;
    }

    protected Map<String, Map<String, Object>> getReport(Map<String, Object> document) {
        Map<String, Map<String, Object>> documents = null;
        if (!isDocument(document)) {
            documents = getArrayRecords(document);
        }
        for (String key : documents.keySet()) {
            for (String rootKey : document.keySet()) {
                if (!rootKey.contains("|")) {
                    Map<String, Object> temp = documents.get(key);
                    temp.put(rootKey, document.get(rootKey));
                    documents.put(key, temp);
                }
            }
        }
        return documents;
    }

    public Map<String, Map<String, Object>> getArrayRecords(Map<String, Object> document) {
        Map<String, Map<String, Object>> documents = new HashMap<>();
        Map<String, Object> record = null;
        for (String key : document.keySet()) {
            String storeKey = key;
            if (key.contains("|")) {
                storeKey = key.substring(0, key.indexOf("|"));
                record = documents.containsKey(storeKey) ? documents.get(storeKey) : new HashMap<>();
                record.put(key, document.get(key));
                documents.put(storeKey, record);
            }
        }
        return documents;
    }

    protected void getHeaders(Node node, String subsidiary, List<String> headers, List<String> params, String mapping, Map<String, String> pivotHeaders) {
        List<SourceTargetMapping> sourceTargetMappingList = sourceTargetMappingRepository.findBySubsidiariesAndNode(subsidiary, String.valueOf(node.getId()));
        if (CollectionUtils.isNotEmpty(sourceTargetMappingList)) {
            if (sourceTargetMappingList.size() == 1) {
                for (SourceTargetParamMapping sourceTargetParamMapping : sourceTargetMappingList.get(0).getSourceTargetParamMappings()) {
                    processHeadersInternal(sourceTargetParamMapping, headers, params, pivotHeaders);
                }
            } else {
                for (SourceTargetMapping sourceTargetMapping : sourceTargetMappingList) {
                    if (sourceTargetMapping.getIdentifier().contains(mapping)) {
                        for (SourceTargetParamMapping sourceTargetParamMapping : sourceTargetMapping.getSourceTargetParamMappings()) {
                            processHeadersInternal(sourceTargetParamMapping, headers, params, pivotHeaders);
                        }
                    }
                }
            }
        }
    }

    private void processHeadersInternal(SourceTargetParamMapping sourceTargetParamMapping, List<String> headers, List<String> params, Map<String, String> pivotHeaders) {
        headers.add(sourceTargetParamMapping.getHeader());
        params.add(sourceTargetParamMapping.getParam());
        if (sourceTargetParamMapping.getIsPivot() != null && sourceTargetParamMapping.getIsPivot()) {
            pivotHeaders.put(sourceTargetParamMapping.getParam(), sourceTargetParamMapping.getHeader());
        }
    }

    private ReportDto getReportDto(Map<String, String> reportData) {
        //TODO check if the record is correctly fetched
        Subsidiary subsidiary = subsidiaryRepository.findByRecordId(reportData.get("subsidiary"));
        ReportDto reportDto = modelMapper.map(reportData, ReportDto.class);
        if (subsidiary != null) {
            reportDto.setSubsidiary(subsidiary);
        }
        String mappingData = reportData.get("mapping");
        String interfaceName = reportData.get("interfaceName");
        reportDto.setNodePath(interfaceName);
        Node node = nodeRepository.findByPath(TOOLKIT + mappingData.replaceAll("Mapping", ""));
        if (null == node) {
            try {
                SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(mappingData);
                if (null != sourceTargetMapping) {
                    //TODO check if the record is correctly fetched
                    Node nodeOptional = nodeRepository.findByRecordId(sourceTargetMapping.getNode());
                    if (nodeOptional != null) {
                        node = nodeOptional;
                    }
                }
            } catch (Exception e) {
                LOG.error("Exception - ", e);
            }
        }
        String currentUserSession = reportData.get("cronId");
        reportDto.setCurrentSessionId(currentUserSession);
        //TODO check if the record is correctly fetched
        reportDto.setCurrentNode(node != null ? node.getRecordId() : null);
        if (StringUtils.isNotEmpty(reportData.get("sites"))) {
            reportDto.setSites(Arrays.asList(reportData.get("sites").split(",")));
        }
        if (StringUtils.isNotEmpty(reportData.get("shortcuts"))) {
            reportDto.setShortcuts(Arrays.asList(reportData.get("shortcuts").split(",")));
        }
        reportDto.setEmail(reportData.get("emails"));
        return reportDto;
    }
}
