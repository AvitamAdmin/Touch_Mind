package com.cheil.web.controllers;

import com.cheil.core.mongo.model.DataRelation;
import com.cheil.core.mongo.model.DataRelationParams;
import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.model.DataSourceInput;
import com.cheil.core.mongo.model.Site;
import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.model.SourceTargetParamMapping;
import com.cheil.core.mongo.repository.DataRelationRepository;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.SiteRepository;
import com.cheil.core.mongo.repository.SourceTargetMappingRepository;
import com.cheil.core.service.ExcelFileService;
import com.cheil.form.TradeInForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/generator")
public class GeneratorController extends BaseController {

    private static final String DELIM = "\r\n";
    Logger logger = LoggerFactory.getLogger(GeneratorController.class);

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ExcelFileService excelFileService;

    @Autowired
    private DataRelationRepository dataRelationRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    private static int getCheckDigit(String input) {
        int sum = 0;
        for (int counter = 13; counter >= 0; counter--) {
            String digitString = input.substring(counter, counter + 1);
            int digit = Integer.valueOf(digitString);

            if (counter % 2 == 0) {
                sum += digit;
            } else {
                sum += sumUpDigits(digit);
            }
        }
        sum *= 9;
        return sum % 10;
    }

    private static int sumUpDigits(int digit) {
        // TODO Auto-generated method stub
        int sum = 0;
        digit *= 2;
        while (digit > 0) {
            sum += digit % 10;
            digit /= 10;
        }
        return sum;
    }

    private void multiColumnFrontendAlignment(Model model, List<SourceTargetParamMapping> filteredMappingParams) {
        int listsRequired = 1;
        int paramsCount = filteredMappingParams.size();

        if (paramsCount % 4 == 0) {
            listsRequired = paramsCount / 4;
        } else {
            listsRequired = (paramsCount / 4) + 1;
        }
        List<List<SourceTargetParamMapping>> allParams = new ArrayList<>();

        for (int i = 0; i < listsRequired * 4; i += 4) {
            List<SourceTargetParamMapping> params = new ArrayList<>();
            params.addAll(filteredMappingParams.subList(i,
                    Math.min(i + 4, filteredMappingParams.size())));
            allParams.add(params);
        }
        Map<String, String> paramMap = new HashMap<>();
        for (List<SourceTargetParamMapping> allParam : allParams) {
            for (SourceTargetParamMapping paramMapping : allParam) {
                DataSource dataSource = dataSourceRepository.findByRecordId(paramMapping.getDataSource());
                List<DataSourceInput> dataSourceInputs = dataSource.getDataSourceInputs();
                if (CollectionUtils.isEmpty(dataSourceInputs)) {
                    paramMap.put(paramMapping.getParam(), "Input Box");
                }
                for (DataSourceInput dataSourceInput : dataSourceInputs) {
                    if (paramMapping.getParam().equalsIgnoreCase(dataSourceInput.getFieldName())) {
                        if (StringUtils.isNotEmpty(dataSourceInput.getFieldValue())) {
                            paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat() + "$%" + dataSourceInput.getFieldValue());
                        } else {
                            paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat());
                        }
                    }
                }
            }
        }
        model.addAttribute("paramMap", paramMap);
        model.addAttribute("allParams", allParams);
    }

    private void populateDynamicParamData(List<Map<String, String>> excelData, Map<String, String> paramMap, List<SourceTargetParamMapping> allParam) {
        for (SourceTargetParamMapping paramMapping : allParam) {
            DataSource dataSource = dataSourceRepository.findByRecordId(paramMapping.getDataSource());
            List<DataSourceInput> dataSourceInputs = dataSource.getDataSourceInputs();
            if (CollectionUtils.isEmpty(dataSourceInputs)) {
                String value = "";
                for (Map<String, String> mapData : excelData) {
                    String mapVal = mapData.get(paramMapping.getParam());
                    if (StringUtils.isNotEmpty(mapVal)) {
                        value = value + "\n" + mapVal;
                    }
                }
                paramMap.put(paramMapping.getParam(), "Input Box" + "$%" + value);
            }
            for (DataSourceInput dataSourceInput : dataSourceInputs) {
                String fieldName = dataSourceInput.getFieldName();
                if (paramMapping.getParam().equalsIgnoreCase(fieldName)) {
                    String value = "";
                    for (Map<String, String> mapData : excelData) {
                        String mapVal = mapData.get(fieldName);
                        if (StringUtils.isNotEmpty(mapVal)) {
                            value = value + "\n" + mapVal;
                        }
                    }
                    if (StringUtils.isNotEmpty(dataSourceInput.getFieldValue())) {
                        paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat() + "$%" + dataSourceInput.getFieldValue() + "$%" + value);
                    } else {
                        if (StringUtils.isNotEmpty(value)) {
                            paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat() + "$%" + value);
                        } else {
                            paramMap.put(fieldName, dataSourceInput.getInputFormat());
                        }
                    }

                }
            }
        }
    }

    private void populateSearchResultData(Map<String, String[]> parameterMap, Map<String, String> paramMap, List<SourceTargetParamMapping> allParam) {
        for (SourceTargetParamMapping paramMapping : allParam) {
            DataSource dataSource = dataSourceRepository.findByRecordId(paramMapping.getDataSource());
            List<DataSourceInput> dataSourceInputs = dataSource.getDataSourceInputs();
            if (CollectionUtils.isEmpty(dataSourceInputs)) {
                String value = "";
                String[] values = parameterMap.get(paramMapping.getParam());
                if (values != null) {
                    String mapVal = values[0];
                    if (StringUtils.isNotEmpty(mapVal)) {
                        value = mapVal;
                    }
                    paramMap.put(paramMapping.getParam(), "Input Box" + "$%" + value);
                }
            }
            for (DataSourceInput dataSourceInput : dataSourceInputs) {
                String fieldName = dataSourceInput.getFieldName();
                if (paramMapping.getParam().equalsIgnoreCase(fieldName)) {
                    String value = "";
                    String[] values = parameterMap.get(paramMapping.getParam());
                    if (values != null) {
                        String mapVal = values[0];
                        if (StringUtils.isNotEmpty(mapVal)) {
                            value = mapVal;
                        }
                    }
                    if (StringUtils.isNotEmpty(dataSourceInput.getFieldValue())) {
                        paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat() + "$%" + dataSourceInput.getFieldValue() + "$%" + value);
                    } else {
                        if (StringUtils.isNotEmpty(value)) {
                            paramMap.put(dataSourceInput.getFieldName(), dataSourceInput.getInputFormat() + "$%" + value);
                        } else {
                            paramMap.put(fieldName, dataSourceInput.getInputFormat());
                        }
                    }

                }
            }
        }
    }

    @GetMapping("/imeigenerate")
    @ResponseBody
    public List<String> generate() throws ParseException {
        List<String> imeiNumbers = new ArrayList<>();
        Random myRandom = new Random();

        for (int i = 0; i < 5; i++) {
            StringBuilder imeiNumber = new StringBuilder(14);
            imeiNumber.append("864898034");

            for (int counter = 0; counter < 5; counter++) {
                imeiNumber.append(1 + myRandom.nextInt(8));
            }

            int generatedCheckDigit = getCheckDigit(imeiNumber.toString());
            imeiNumbers.add(imeiNumber.append(generatedCheckDigit).toString());
        }
        return imeiNumbers;
    }

    @GetMapping("/**")
    @ResponseBody
    public String tradeInSelect(Model model, HttpServletRequest request) {
        model.addAttribute("editForm", new TradeInForm());
        String path = request.getServletPath();
        model.addAttribute("path", path);

        SourceTargetMapping dataMapping = sourceTargetMappingRepository.findByNode(String.valueOf(nodeRepository.findByPath(path).getId()));
        if (dataMapping != null) {
            List<String> subsidiaries = dataMapping.getSubsidiaries();
            Map<String, Map<String, List<String>>> subSiteMap = new HashMap<>();
            for (String subsidiary : subsidiaries) {
                //TODO Check if this is correctly fetching the data
                List<Site> sites = siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary, true);
                if (CollectionUtils.isNotEmpty(sites)) {
                    Map<String, List<String>> siteChannelMap = new HashMap<>();

                    for (Site site : sites) {
                        List<String> siteList = new ArrayList<>();
                        String siteChannel = site.getSiteChannel();
                        siteChannel = StringUtils.isNotEmpty(siteChannel) ? siteChannel : "NoChannel";
                        if (siteChannelMap.containsKey(siteChannel)) {
                            siteList.addAll(siteChannelMap.get(siteChannel));
                        }
                        siteList.add(site.getIdentifier());
                        siteChannelMap.put(siteChannel, siteList);
                    }
                    subSiteMap.put(subsidiary, siteChannelMap);
                }
            }
            model.addAttribute("subsidiaries", subSiteMap);
            List<SourceTargetParamMapping> mappingParams = dataMapping.getSourceTargetParamMappings();
            List<SourceTargetParamMapping> filteredMappingParams = new ArrayList<>();

            mappingParams.forEach(sourceTargetParamMapping -> {
                String param = sourceTargetParamMapping.getParam();
                DataSource dataSource = dataSourceRepository.findByRecordId(sourceTargetParamMapping.getDataSource());
                dataSource.getDataSourceInputs().forEach(dataSourceInput -> {
                    if (dataSourceInput.getFieldName().equalsIgnoreCase(param)) {
                        filteredMappingParams.add(sourceTargetParamMapping);
                    }
                });

            });
            if (CollectionUtils.isNotEmpty(filteredMappingParams)) {
                multiColumnFrontendAlignment(model, filteredMappingParams);
            }
        }
        return "generator/tradeInSelect";
    }

    @PostMapping("/**")
    @ResponseBody
    public String generateResults(Model model, HttpServletRequest request) throws ParseException {

        String path = request.getServletPath();
        model.addAttribute("path", path);

        SourceTargetMapping dataMapping = sourceTargetMappingRepository.findByNode(String.valueOf(nodeRepository.findByPath(path).getId()));
        DataRelation dataRelation = dataRelationRepository.findByIdentifier(dataMapping.getDataRelation());

        final Map<String, String[]> parameterMap = request.getParameterMap();
        final Map<String, List<String>> result = new HashMap<>();
        List<String> headers = new ArrayList<>();
        List<SourceTargetParamMapping> mappingParams = dataMapping.getSourceTargetParamMappings();
        if (MapUtils.isNotEmpty(parameterMap)) {
            HttpSession session = request.getSession();
            session.setAttribute("populatedValues", parameterMap);

            for (SourceTargetParamMapping param : mappingParams) {
                String paramString = param.getParam();

                if (paramString.replaceAll(" ", "").equalsIgnoreCase("filename")) {
                    model.addAttribute("fileName", parameterMap.get(paramString)[0]);
                } else {
                    headers.add(paramString);
                }
                if (parameterMap.get(paramString) != null && parameterMap.get(paramString).length > 0) {
                    DataSource dataSource = dataSourceRepository.findByRecordId(param.getDataSource());
                    List<DataSourceInput> dateFieldsDataSources = dataSource.getDataSourceInputs().stream().filter(dataSourceInput -> dataSourceInput.getInputFormat().equalsIgnoreCase("Date and Time selector")).collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(dateFieldsDataSources))
                        populateResultData(parameterMap, result, paramString, dateFieldsDataSources);
                    else {
                        result.put(paramString, getValuesUsingSeparator(parameterMap.get(paramString)[0]));
                    }
                }
            }
        }

        Map<Integer, List<String>> valuesMap = new TreeMap<>();


        List<String> primaryKeys = new ArrayList<>();

        for (DataRelationParams dataRelationParams : dataRelation.getDataRelationParams()) {
            String key = dataRelationParams.getSourceKeyOne();
            if (key.contains(",")) {
                String[] keys = key.split(",");
                key = keys[0];
                primaryKeys.add(key);
            } else {
                primaryKeys.add(key);
            }
        }

        if (CollectionUtils.isNotEmpty(primaryKeys)) {
            Map<Integer, List<List<String>>> clusterData = new HashMap();
            List<List<String>> input = new ArrayList<>();
            for (String key : primaryKeys) {
                input.add(result.get(key));
            }
            if (checkIfClusterData(input)) {
                clusterData = getClusterRecords(input);
            } else {
                clusterData.put(0, input);
            }
            for (List<List<String>> clusterList : clusterData.values()) {
                generateReport(result, headers, valuesMap, primaryKeys, clusterList);
            }
        }
        model.addAttribute("valuesMap", valuesMap);
        model.addAttribute("columnHeaders", headers);
        return "generator/tradeInResults";
    }

    private void generateReport(Map<String, List<String>> result, List<String> headers, Map<Integer, List<String>> valuesMap, List<String> primaryKeys, List<List<String>> clusterList) {
        int totalLoopCount = 1;
        int maxCount = 0;
        for (List<String> cluster : clusterList) {
            if (maxCount < cluster.size()) {
                maxCount = cluster.size();
            }
            totalLoopCount *= cluster.size();
        }
        List<List<String>> allCombinations = populateAllCombinations(clusterList);
        if (CollectionUtils.isNotEmpty(allCombinations)) {
            int secCount = 0;
            for (int i = 0; i < totalLoopCount; i++) {
                if (maxCount > 0) {
                    if (i > 0 && i % maxCount == 0) {
                        ++secCount;
                    }
                }
                int count = 0;
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    List<String> splitValues = result.get(header);
                    if (CollectionUtils.isNotEmpty(splitValues)) {
                        if (primaryKeys.contains(header)) {
                            values.add(allCombinations.get(i).get(count));
                            count++;
                        } else {
                            if (splitValues.size() > secCount) {
                                values.add(splitValues.get(secCount));
                            } else {
                                values.add(splitValues.get(0));
                            }
                        }
                    } else {
                        values.add("");
                    }
                }
                valuesMap.put(i, values);
            }
        }
    }

    private Map<Integer, List<List<String>>> getClusterRecords(List<List<String>> input) {
        Map<Integer, List<List<String>>> clusterList = new HashMap();

        for (List<String> clusterData : input) {
            int i = 0;
            List<List<String>> finalList = new ArrayList<>();
            List<String> dynamicList = new ArrayList<>();
            for (String clusterSplit : clusterData) {
                if (!clusterSplit.equalsIgnoreCase("END")) {
                    dynamicList.add(clusterSplit);
                } else {
                    finalList.add(dynamicList);
                    if (clusterList.containsKey(i)) {
                        finalList.addAll(clusterList.get(i));
                    }
                    clusterList.put(i, finalList);
                    dynamicList = new ArrayList<>();
                    finalList = new ArrayList<>();
                    i++;
                }
            }
        }
        return clusterList;
    }

    private boolean checkIfClusterData(List<List<String>> input) {
        boolean isCluster = false;
        for (List<String> data : input) {
            if (data.contains("END")) {
                isCluster = true;
                break;
            }
        }
        return isCluster;
    }

    private void populateResultData(Map<String, String[]> parameterMap, Map<String, List<String>> result, String paramString, List<DataSourceInput> dateFieldsDataSources) throws ParseException {
        for (DataSourceInput dateFieldDatasource : dateFieldsDataSources) {
            if (dateFieldDatasource.getFieldName().equalsIgnoreCase(paramString)) {
                String dateFormat = dateFieldDatasource.getFieldValue();
                if (StringUtils.isNotEmpty(dateFormat)) {
                    DateFormat outFormat = new SimpleDateFormat(dateFormat.replaceAll("DD", "dd"));
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
                    List<String> values = getValuesUsingSeparator(parameterMap.get(paramString)[0]);
                    List<String> newValues = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(values)) {
                        String formattedDate = outFormat.format(inputFormat.parse(values.get(0)));
                        newValues.add(formattedDate);
                    }
                    result.put(paramString, newValues);
                } else {
                    result.put(paramString, getValuesUsingSeparator(parameterMap.get(paramString)[0]));
                }
            } else {
                if (!result.containsKey(paramString)) {
                    result.put(paramString, getValuesUsingSeparator(parameterMap.get(paramString)[0]));
                }
            }
        }
    }

    private List<List<String>> populateAllCombinations(List<List<String>> input) {
        int[] indexes = new int[input.size()];

        List<String> base = new ArrayList<>();
        for (List<String> dataInput : input) {
            if (CollectionUtils.isNotEmpty(dataInput)) {
                base.add(dataInput.get(0));
            }
        }

        List<List<String>> allCombinations = new ArrayList<>();

        while (CollectionUtils.isNotEmpty(base) && base.size() >= indexes.length) {
            allCombinations.add(new ArrayList<>(base));

            int k = indexes.length - 1;
            for (; k >= 0; k--) {
                indexes[k] += 1;
                if (indexes[k] < input.get(k).size()) {
                    base.set(k, input.get(k).get(indexes[k]));
                    break;
                }
                indexes[k] = 0;
                base.set(k, input.get(k).get(indexes[k]));
            }
            if (k < 0) {
                break;
            }
        }
        return allCombinations;
    }

    public List<String> getValuesUsingSeparator(String values) {
        List<String> valList = new ArrayList<>();
        if (StringUtils.isNotEmpty(values)) {
            String[] arrValues = values.split(DELIM);
            Collections.addAll(valList, arrValues);
        }
        return valList;
    }

    @PostMapping("/{path}/upload")
    @ResponseBody
    public String uploadFile(@PathVariable("path") String path, @RequestParam("file") MultipartFile file, Model model) {
        path = "/generator/" + path;
        model.addAttribute("path", path);

        try {
            List<Map<String, String>> excelData = excelFileService.loadExcelData(file);

            SourceTargetMapping dataMapping = sourceTargetMappingRepository.findByNode(String.valueOf(nodeRepository.findByPath(path).getId()));
            if (dataMapping != null) {
                List<String> subsidiaries = dataMapping.getSubsidiaries();
                Map<String, Map<String, List<String>>> subSiteMap = new HashMap<>();
                for (String subsidiary : subsidiaries) {
                    populateSiteMapData(subSiteMap, subsidiary);
                }
                model.addAttribute("subsidiaries", subSiteMap);
                List<SourceTargetParamMapping> mappingParams = dataMapping.getSourceTargetParamMappings();
                List<SourceTargetParamMapping> filteredMappingParams = new ArrayList<>();

                mappingParams.forEach(sourceTargetParamMapping -> {
                    String param = sourceTargetParamMapping.getParam();
                    DataSource dataSource = dataSourceRepository.findByRecordId(sourceTargetParamMapping.getDataSource());
                    dataSource.getDataSourceInputs().forEach(dataSourceInput -> {
                        if (dataSourceInput.getFieldName().equalsIgnoreCase(param)) {
                            filteredMappingParams.add(sourceTargetParamMapping);
                        }
                    });

                });
                if (CollectionUtils.isNotEmpty(filteredMappingParams)) {

                    int listsRequired = 1;
                    int paramsCount = filteredMappingParams.size();

                    if (paramsCount % 4 == 0) {
                        listsRequired = paramsCount / 4;
                    } else {
                        listsRequired = (paramsCount / 4) + 1;
                    }
                    List<List<SourceTargetParamMapping>> allParams = new ArrayList<>();

                    for (int i = 0; i < listsRequired * 4; i += 4) {
                        List<SourceTargetParamMapping> params = new ArrayList<>();
                        params.addAll(filteredMappingParams.subList(i,
                                Math.min(i + 4, filteredMappingParams.size())));
                        allParams.add(params);
                    }
                    Map<String, String> paramMap = new HashMap<>();
                    for (List<SourceTargetParamMapping> allParam : allParams) {
                        populateDynamicParamData(excelData, paramMap, allParam);
                    }
                    model.addAttribute("paramMap", paramMap);
                    model.addAttribute("allParams", allParams);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "generator/tradeInSelect";
    }

    private void populateSiteMapData(Map<String, Map<String, List<String>>> subSiteMap, String subsidiary) {
        //TODO Check if this is correctly fetching the data
        List<Site> sites = siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(subsidiary, true);
        if (CollectionUtils.isNotEmpty(sites)) {
            Map<String, List<String>> siteChannelMap = new HashMap<>();

            for (Site site : sites) {
                List<String> siteList = new ArrayList<>();
                if (siteChannelMap.containsKey(site.getSiteChannel())) {
                    siteList.addAll(siteChannelMap.get(site.getSiteChannel()));
                }
                siteList.add(site.getIdentifier());
                siteChannelMap.put(site.getSiteChannel(), siteList);
            }
            subSiteMap.put(subsidiary, siteChannelMap);
        }
    }

    @GetMapping("/{path}/back")
    @ResponseBody
    public String clickBack(@PathVariable("path") String path, HttpServletRequest request, Model model) {
        path = "/generator/" + path;
        model.addAttribute("path", path);

        try {
            HttpSession session = request.getSession();
            Object mapData = session.getAttribute("populatedValues");

            Map<String, String[]> parameterMap = new HashMap<>();

            if (mapData != null) {
                parameterMap = (Map<String, String[]>) mapData;
            }

            SourceTargetMapping dataMapping = sourceTargetMappingRepository.findByNode(String.valueOf(nodeRepository.findByPath(path).getId()));
            if (dataMapping != null) {
                populateFilteredData(model, parameterMap, dataMapping);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "generator/tradeInSelect";
    }

    private void populateFilteredData(Model model, Map<String, String[]> parameterMap, SourceTargetMapping dataMapping) {
        List<String> subsidiaries = dataMapping.getSubsidiaries();
        Map<String, Map<String, List<String>>> subSiteMap = new HashMap<>();
        for (String subsidiary : subsidiaries) populateSiteMapData(subSiteMap, subsidiary);
        model.addAttribute("subsidiaries", subSiteMap);
        List<SourceTargetParamMapping> mappingParams = dataMapping.getSourceTargetParamMappings();
        List<SourceTargetParamMapping> filteredMappingParams = new ArrayList<>();

        mappingParams.forEach(sourceTargetParamMapping -> {
            String param = sourceTargetParamMapping.getParam();
            DataSource dataSource = dataSourceRepository.findByRecordId(sourceTargetParamMapping.getDataSource());
            dataSource.getDataSourceInputs().forEach(dataSourceInput -> {
                if (dataSourceInput.getFieldName().equalsIgnoreCase(param)) {
                    filteredMappingParams.add(sourceTargetParamMapping);
                }
            });

        });
        if (CollectionUtils.isNotEmpty(filteredMappingParams)) {

            int listsRequired = 1;
            int paramsCount = filteredMappingParams.size();

            if (paramsCount % 4 == 0) {
                listsRequired = paramsCount / 4;
            } else {
                listsRequired = (paramsCount / 4) + 1;
            }
            List<List<SourceTargetParamMapping>> allParams = new ArrayList<>();

            for (int i = 0; i < listsRequired * 4; i += 4) {
                List<SourceTargetParamMapping> params = new ArrayList<>();
                params.addAll(filteredMappingParams.subList(i,
                        Math.min(i + 4, filteredMappingParams.size())));
                allParams.add(params);
            }
            Map<String, String> paramMap = new HashMap<>();
            for (List<SourceTargetParamMapping> allParam : allParams) {
                populateSearchResultData(parameterMap, paramMap, allParam);
            }
            model.addAttribute("paramMap", paramMap);
            model.addAttribute("allParams", allParams);
        }
    }
}
