package com.cheil.web.controllers;

import com.cheil.core.mongo.model.DataSource;
import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.model.PriceDocuments;
import com.cheil.core.mongo.model.SchedulerJob;
import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.model.baseEntity.BaseEntity;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.NodeRepository;
import com.cheil.core.mongo.repository.SchedulerJobRepository;
import com.cheil.core.mongo.repository.SourceTargetMappingRepository;
import com.cheil.core.mongo.repository.generic.GenericRepository;
import com.cheil.core.service.RepositoryService;
import com.cheil.tookit.service.ReportService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/chart")
public class ChartReportController extends ScheduledReportController {

    @Autowired
    protected NodeRepository nodeRepository;
    @Autowired
    ReportService reportService;
    @Autowired
    private SchedulerJobRepository schedulerJobRepository;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @GetMapping
    @ResponseBody
    public String getCharts() {
        return "chart";
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Integer> data(@RequestParam("id") String id,
                                     @RequestParam("key") String key, Model model) {
        Map<String, Integer> chart = new HashMap<>();
        if (key.contains("-")) {
            id = key.split("-")[0];
            key = key.split("-")[1];
        }
        if (id.contains("order") || id.equalsIgnoreCase("returnRequestedApi")) {
            getOrderReport(model, chart, key, id);
        } else {
            getReportData(model, chart, key, id);
        }
        return chart;
    }


    Map<String, Integer> getReportData(Model model, Map<String, Integer> chart, String key, String id) {

        SchedulerJob schedule = schedulerJobRepository.findByNodePath(SCHEDULE_PATH + id);
        if (schedule != null) {
            String pricePercent = "50";
            if (id.contains("-")) {
                String[] paths = id.split("-");
                pricePercent = paths[1];
                id = id.split("-")[0];
            }
            Node node = nodeRepository.findByPath(TOOLKIT + id);
            if (null == node) {
                SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(schedule.getMapping());
                if (null != sourceTargetMapping) {
                    //TODO Check if this is correctly fetching the data
                    Node nodeOptional = nodeRepository.findByRecordId(sourceTargetMapping.getNode());
                    if (nodeOptional != null) {
                        node = nodeOptional;
                    }
                }
            }
            String currentUserSession = schedule.getCronId();
            GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getRecordId());
            List<BaseEntity> documents = genericRepository.getBySessionId(currentUserSession);
            Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, schedule.getSubsidiary(), schedule.getMapping());
            model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
            model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
            if (CollectionUtils.isNotEmpty(documents)) {
                BaseEntity entity = documents.get(0);
                if (entity instanceof PriceDocuments && currentUserSession.contains("%_")) {
                    String[] parts = currentUserSession.split("%_");
                    if (parts.length > 0) {
                        double discount = Double.valueOf(parts[0]);
                        List<BaseEntity> priceDocuments = new ArrayList<>();
                        for (BaseEntity document : documents) {
                            Map<String, Object> records = document.getRecords();
                            if (records.containsKey(PRODUCT_DISCOUNT_VALUE) && Double.valueOf(String.valueOf(records.get(PRODUCT_DISCOUNT_VALUE)).replaceAll(
                                    "%", "")) >= discount && discount >= Double.valueOf(pricePercent)) {
                                priceDocuments.add(document);
                            }
                        }
                        return populateReportChartData(reportService.adjustReportForHeaders(priceDocuments, (List<String>) model.getAttribute(P_VALUES)), chart, key);
                    }
                } else {
                    return populateReportChartData(reportService.adjustReportForHeaders(documents, (List<String>) model.getAttribute(P_VALUES)), chart, key);
                }
            }
        }
        return null;
    }

    Map<String, Integer> getOrderReport(Model model, Map<String, Integer> chart, String key, String id) {

        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(id + "Mapping");
        //TODO Check if this is correctly fetching the data
        Node node = nodeRepository.findByRecordId(mapping.getNode());
        DataSource dataSource = dataSourceRepository.findByRecordId(mapping.getSourceTargetParamMappings().get(0).getDataSource());
        String currentUserSession = dataSource.getIdentifier();
        //TODO Check if this is correctly fetching the data
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getRecordId());
        List<BaseEntity> documents = genericRepository.getBySessionId(currentUserSession);
        Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, mapping.getSubsidiaries().get(0), id + "Mapping");
        model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
        model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
        return populateReportChartData(reportService.adjustReportForHeaders(documents, (List<String>) model.getAttribute(P_VALUES)), chart, key);
    }


    Map<String, Integer> populateReportChartData(List<Map<String, Object>> documents, Map<String, Integer> chart, String key) {

        for (Map<String, Object> document : documents) {
            String value = String.valueOf(document.get(key));
            if (null == value || value.equalsIgnoreCase("null")) {
                if (key.equalsIgnoreCase("price")) {
                    key = "§price";
                }
                for (String docKey : document.keySet()) {
                    if (docKey.toLowerCase().contains(key.toLowerCase())) {
                        value = String.valueOf(document.get(docKey));
                        break;
                    }
                }
            }
            value = value.contains(".") ? value : value.stripLeading();
            if (chart.containsKey(value)) {
                chart.put(value, chart.get(value) + 1);
            } else {
                chart.put(value, 1);
            }
        }
        return chart;
    }
}