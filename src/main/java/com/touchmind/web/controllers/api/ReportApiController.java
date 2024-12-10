package com.touchmind.web.controllers.api;

import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.tookit.service.ReportService;
import com.touchmind.web.controllers.toolkit.BaseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ReportApiController extends BaseController {
    @Autowired
    ReportService reportService;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;

    @GetMapping("/report/{id}")
    @ResponseBody
    public String processReportJson(@PathVariable String id, @RequestParam String skus, @RequestParam String subsidiary, @RequestParam String sites,
                                    Model model, HttpServletRequest request) throws JsonProcessingException {

        Node node = nodeRepository.findByPath(TOOLKIT + id);
        String currentUserSession = reportService.getCurrentUserSessionId(request, id);
        ReportDto reportDto = new ReportDto();
        reportDto.setCurrentSessionId(currentUserSession);
        reportDto.setMapping(id + "Mapping");
        reportDto.setSubsidiary(getSubsidiaryRepository().findByIdentifier(subsidiary));
        reportDto.setSkus(skus);
        reportDto.setSites(Arrays.asList(sites.split(",")));
        if (null == node) {
            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(reportDto.getMapping());
            if (null != sourceTargetMapping) {
                //TODO Check if this is correctly fetching the data
                Node nodeOptional = nodeRepository.findByRecordId(sourceTargetMapping.getNode());
                if (nodeOptional != null) {
                    node = nodeOptional;
                }
            }
        }
        //TODO Check if this is correctly fetching the data
        reportDto.setCurrentNode(node != null ? node.getRecordId() : null);
        //TODO Check if this is correctly fetching the data
        Map<String, List<String>> headersAndParams = reportService.getHeaders(nodeRepository.findByRecordId(reportDto.getCurrentNode()), reportDto.getSubsidiary().getRecordId(), reportDto.getMapping());
        model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
        model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
        List<BaseEntity> allRecords = reportService.getReport(reportDto);

        List<Map<String, Object>> results = new ArrayList<>();
        results.addAll(reportService.adjustReportForHeaders(allRecords, (List<String>) model.getAttribute(P_VALUES)));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(results);
    }
}
