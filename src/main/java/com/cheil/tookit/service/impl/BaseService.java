package com.cheil.tookit.service.impl;

import com.cheil.core.mongo.dto.ReportDto;
import com.cheil.core.mongo.model.CronHistory;
import com.cheil.core.mongo.model.DataRelation;
import com.cheil.core.mongo.model.DataRelationParams;
import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.model.Subsidiary;
import com.cheil.core.mongo.repository.CronHistoryRepository;
import com.cheil.core.mongo.repository.DataRelationParamsRepository;
import com.cheil.core.mongo.repository.DataRelationRepository;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.SourceTargetMappingRepository;
import com.cheil.core.service.ModelService;
import com.cheil.core.service.XmlService;
import com.cheil.core.service.impl.JsonServiceImpl;
import com.cheil.data.DataServiceFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BaseService {

    public static final String CLUSTER_ID = "clusterId";
    public static final String TEM_SITE = "temSite";
    public static final String TEM_VARIANT = "temVariant";

    public static final String TEM_VARIANT2 = "temVariant2";
    public static final String TEM_VARIANTS = "temVariants";
    public static final String HTTP = "http";
    public static final String RETURNED_NULL_FOR_API = "There was error requesting url , server returned null for api ";

    public static final String TEM_CAT = "temCategory";
    public static final String CURR_PAGE = "currentPage";
    public static final String TEM_IMEI = "temIMEI";

    Logger logger = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    ModelService modelService;
    @Autowired
    CronHistoryRepository cronHistoryRepository;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private DataRelationRepository dataRelationRepository;
    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private DataRelationParamsRepository dataRelationParamsRepository;
    @Autowired
    private JsonServiceImpl baseJsonService;
    @Autowired
    private XmlService xmlService;
    @Autowired
    private DataServiceFactory dataServiceFactory;
    @Autowired
    private Environment env;

    protected String getApi(Map<String, String> hashMap, String template) {
        return hashMap.entrySet().stream().reduce(template, (s, e) -> s.replace("$[" + e.getKey() + "]", e.getValue()),
                (s, s2) -> s);
    }

    protected void processData(ReportDto reportDto) {
        DataRelation dataRelation = StringUtils.isEmpty(reportDto.getCurrentNode()) ? getDataRelationForMappingId(reportDto) : getDataRelationForSubsidiaryAndNode(reportDto.getSubsidiary(), String.valueOf(reportDto.getCurrentNode()), reportDto.getMapping());
        if (dataRelation == null) {
            logger.error("Error occurred while retrieving DataRelation for subsidiary & Node check the configuration ");
            return;
        }
        for (DataRelationParams dataRelationParams : dataRelationParamsRepository.findByDataRelation(dataRelation)) {
            if (dataRelation.getIdentifier().equals(dataRelationParams.getDataRelation().getIdentifier())) {
                processDataSource(reportDto, dataRelationParams);
            }
        }
    }

    private DataRelation getDataRelationForMappingId(ReportDto reportDto) {
        SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(reportDto.getMapping());
        return dataRelationRepository.findByIdentifier(sourceTargetMapping.getDataRelation());
    }

    private void processDataSource(ReportDto reportDto, DataRelationParams dataRelationParams) {
        String relationKeys = dataRelationParams.getSourceKeyOne();
        List<String> variants = StringUtils.isNotEmpty(reportDto.getSkus()) ? modelService.getVariantListForCommaSeparatedSkus(reportDto) : modelService.getVariantsListForModels(reportDto.getShortcuts());
        //TODO check if the record is correctly fetched
        DataSource dataSource = dataSourceRepository.findByRecordId(dataRelationParams.getDataSource());
        if (dataSource != null) {
            reportDto.setRelationKeys(dataRelationParams.getSourceKeyOne());
            String targetProcess = dataSource.getTargetProcess();
            reportDto.setDataSourceParams(dataSource.getSrcInputParams());
            for (String site : reportDto.getSites()) {
                processDataPerSite(reportDto, relationKeys, variants, dataSource, targetProcess, site);
            }
        }
    }

    private void processDataPerSite(ReportDto reportDto, String relationKeys, List<String> variants, DataSource dataSource, String targetProcess, String site) {
        reportDto.setCurrentSite(site);
        Map<String, String> paramMapping = new HashMap<>();
        if (StringUtils.isNotEmpty(reportDto.getCategory())) {
            paramMapping.put(TEM_CAT, reportDto.getCategory());
        }
        if (StringUtils.isNotEmpty(reportDto.getCurrentPage())) {
            paramMapping.put(CURR_PAGE, reportDto.getCurrentPage());
        }
        paramMapping.put(TEM_SITE, site);
        paramMapping.put(CLUSTER_ID, reportDto.getSubsidiary().getCluster());
        if (relationKeys.contains(TEM_VARIANTS)) {
            String delimiter = dataSource.getSeparatorSymbol();
            String commaSeparatedSkus = variants.stream()
                    .collect(Collectors.joining(StringUtils.isNotEmpty(delimiter) ? delimiter : ","));
            paramMapping.put(TEM_VARIANTS, commaSeparatedSkus);
            String api = getApi(paramMapping, dataSource.getSourceAddress());
            if (StringUtils.isNotEmpty(dataSource.getSkuUrl())) {
                reportDto.setSkuUrl(getApi(paramMapping, dataSource.getSkuUrl()));
            }
            if (StringUtils.isNotEmpty(targetProcess)) {
                dataServiceFactory.getService(targetProcess).processApi(reportDto, api);
            } else {
                processCurrentApi(reportDto, api, dataSource.getFormat());
            }
        } else {
            List<String> variants2 = new ArrayList<>();
            if (StringUtils.isNotEmpty(reportDto.getSkus2())) {
                variants2 = List.of(reportDto.getSkus2().split(","));
            }
            int count = variants.size();
            if (CollectionUtils.isNotEmpty(variants2) && variants.size() > variants2.size()) {
                count = variants2.size();
            }
            if (reportDto.getMapping().equalsIgnoreCase("eup2CustomerValidationMapping") && StringUtils.isEmpty(reportDto.getSkus2())) {
                String temVariant2 = env.getProperty("default.temvariant2");
                paramMapping.put(TEM_VARIANT2, temVariant2);
            }
            count = count == 0 ? 1 : count;
            for (int i = 0; i < count; i++) {
                processDataPerSku(reportDto, variants, dataSource, targetProcess, paramMapping, variants2, i);
            }
        }
    }

    private void processDataPerSku(ReportDto reportDto, List<String> variants, DataSource dataSource, String targetProcess, Map<String, String> paramMapping, List<String> variants2, int i) {
        if (CollectionUtils.isNotEmpty(variants)) {
            String variant = variants.get(i);
            reportDto.setCurrentVariant(variant);
            paramMapping.put(TEM_VARIANT, variant);
            paramMapping.put(TEM_VARIANTS, variant);
            if (variant.contains(",")) {
                String[] variantArray = variant.split(",");
                reportDto.setCurrentVariant(variantArray[1].trim());
                paramMapping.put(TEM_VARIANT, variantArray[1].trim());
                paramMapping.put(TEM_VARIANTS, variantArray[1].trim());
                paramMapping.put(TEM_IMEI, variantArray[0].trim());
                reportDto.setTemImei(variantArray[0].trim());
            }
        }

        if (CollectionUtils.isNotEmpty(variants2)) {
            paramMapping.put(TEM_VARIANT2, variants2.get(i));
        }

        String api = getApi(paramMapping, dataSource.getSourceAddress());
        if (StringUtils.isNotEmpty(dataSource.getSkuUrl())) {
            reportDto.setSkuUrl(getApi(paramMapping, dataSource.getSkuUrl()));
        }
        if (StringUtils.isNotEmpty(targetProcess)) {
            dataServiceFactory.getService(targetProcess).processApi(reportDto, api);
        } else {
            processCurrentApi(reportDto, api, dataSource.getFormat());
        }
    }

    protected void processCurrentApi(ReportDto reportDto, String api, String format) {
        switch (format) {
            case "XML":
                InputStream inputStream = getInputStreamForXml(api);
                if (inputStream == null) {
                    logger.error(RETURNED_NULL_FOR_API + api);
                    return;
                }
                String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                        .lines().parallel().collect(Collectors.joining("\n"));
                org.jsoup.nodes.Document doc = Jsoup.parse(jsonString, Parser.xmlParser());
                xmlService.processXmlData(doc, reportDto);
                break;
            case "JSON":

                if (api.contains("callback=jQuery")) {
                    String json = jsonExcludingCallback(getJsonFromInputStream(api));
                    if (json == null) {
                        logger.error(RETURNED_NULL_FOR_API + api);
                        return;
                    }
                    baseJsonService.processJsonData(json, reportDto);
                } else {
                    inputStream = getInputStreamForJson(api);
                    if (inputStream == null) {
                        logger.error(RETURNED_NULL_FOR_API + api);
                        return;
                    }
                    baseJsonService.processJsonData(inputStream, reportDto);
                }
                break;
            default:
        }
    }

    private String jsonExcludingCallback(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return json.substring(json.indexOf("(") + 1, json.lastIndexOf(")"));
    }

    protected DataRelation getDataRelationForSubsidiaryAndNode(Subsidiary subsidiary, String nodeId, String mapping) {
        List<SourceTargetMapping> sourceTargetMappingList = sourceTargetMappingRepository.findBySubsidiariesAndNode(subsidiary.getRecordId(), String.valueOf(nodeId));
        if (CollectionUtils.isEmpty(sourceTargetMappingList)) {
            return null;
        }
        if (sourceTargetMappingList.size() == 1) {
            String dataRelationId = sourceTargetMappingList.get(0).getDataRelation();
            return dataRelationRepository.findByIdentifier(dataRelationId);
        } else {
            for (SourceTargetMapping sourceTargetMapping : sourceTargetMappingList) {
                if (sourceTargetMapping.getIdentifier().contains(mapping)) {
                    String dataRelationId = sourceTargetMapping.getDataRelation();
                    return dataRelationRepository.findByIdentifier(dataRelationId);
                }
            }
        }
        return null;
    }

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

    protected String getJsonFromInputStream(String api) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(getInputStreamForJson(api), writer, Charsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
        return writer.toString();
    }

    public void saveCronHistory(String currentSessionId, String subId, String email, int processedSkus, String jobTime, String status, String errorMsg, String schedulerId) {
        CronHistory cronHistory = cronHistoryRepository.findBySessionId(currentSessionId);
        if (null == cronHistory) {
            cronHistory = new CronHistory();
        }
        cronHistory.setSessionId(currentSessionId);
        cronHistory.setScheduler(schedulerId);
        cronHistory.setSubsidiary(subId);
        cronHistory.setEmail(email);
        cronHistory.setProcessedSkus(processedSkus);
        cronHistory.setJobTime(jobTime);
        cronHistory.setCronStatus(status);
        cronHistory.setErrorMsg(errorMsg);
        cronHistoryRepository.save(cronHistory);
    }
}
