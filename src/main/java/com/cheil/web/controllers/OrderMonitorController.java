package com.cheil.web.controllers;

import com.cheil.core.mongo.model.Node;
import com.cheil.core.mongo.model.SourceTargetMapping;
import com.cheil.core.mongo.repository.DataSourceRepository;
import com.cheil.core.mongo.repository.SourceTargetMappingRepository;
import com.cheil.core.service.RepositoryService;
import com.cheil.tookit.service.ReportService;
import com.cheil.web.controllers.toolkit.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/ordermonitor")
public class OrderMonitorController extends BaseController {

    @Autowired
    ReportService reportService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @GetMapping("/{id}")
    @ResponseBody
    public String processReport(@PathVariable(required = true) String id, Model model) throws IOException {

        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(id + "Mapping");
        Node node = nodeRepository.findByRecordId(mapping.getNode());
        Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, mapping.getSubsidiaries().get(0), id + "Mapping");
        model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
        model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
        model.addAttribute("reportId", id);
        List<String> columnNames = (List<String>) model.getAttribute(P_NAMES);
        //Set<String> columns = new TreeSet(columnNames);
        model.addAttribute("columnNames", columnNames);
        return "ordermonitor/reportContent";
    }

    /*@GetMapping("/ajax/{id}")
    @ResponseBody
    public String processReportJson(@PathVariable(required = true) String id, @RequestParam("draw") Integer draw, @RequestParam("start") Integer start,
                                    @RequestParam("length") Integer length, @RequestParam Map<String, String> allRequestParams,
                                    Model model) {

        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(id + "Mapping");
        //TODO Check if this is correctly fetching the data
        Node node = nodeRepository.findByRecordId(mapping.getNode());
        DataSource dataSource = dataSourceRepository.findByRecordId(mapping.getSourceTargetParamMappings().get(0).getDataSource());
        String currentUserSession = dataSource.getIdentifier();
        //TODO Check if this is correctly fetching the data
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getRecordId());
        List<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession);
        Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, mapping.getSubsidiaries().get(0), id + "Mapping");
        model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
        model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
        List<Map<String, Object>> results = new ArrayList<>();
        if (length > 0) {
            Pageable paging = PageRequest.of(start / length, length);
            List<BaseEntity> documents = genericRepository.getBySessionId(currentUserSession, paging);
            results = reportService.adjustReportForHeaders(documents, (List<String>) model.getAttribute(P_VALUES));
        } else {
            results = reportService.adjustReportForHeaders(allRecords, (List<String>) model.getAttribute(P_VALUES));
        }

        JSONObject jsonObject = getJsonFormattedData(draw, model, allRecords, results, allRequestParams);
        return jsonObject.toString();
    }*/
}