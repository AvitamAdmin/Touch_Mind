package com.touchMind.web.controllers.toolkit;

import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.dto.ReportWsDto;
import com.touchMind.core.mongo.dto.SourceTargetMappingDto;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.Site;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.tookit.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/toolkit")
public class ReportController extends BaseController {
    @Autowired
    ReportService reportService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private SiteRepository siteRepository;

    @PostMapping(value = "/reportResult")
    @ResponseBody
    public ReportWsDto processReportJson(@RequestBody ReportDto reportDto, HttpServletRequest request) {
        ReportWsDto reportWsDto = new ReportWsDto();
        String currentUserSession = reportService.getCurrentUserSessionId(request, reportDto.getIdentifier());
        Node node = nodeRepository.findByPath(TOOLKIT + reportDto.getIdentifier());
        reportDto.setCurrentSessionId(currentUserSession);
        reportDto.setMapping(reportDto.getIdentifier() + "Mapping");
        if (null == node) {
            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(reportDto.getMapping());
            if (null != sourceTargetMapping) {
                Node nodeOptional = nodeRepository.findByIdentifier(sourceTargetMapping.getNode());
                if (nodeOptional != null) {
                    node = nodeOptional;
                }
            }
        }
        reportDto.setCurrentNode(node != null ? node.getIdentifier() : null);
        reportDto.setVoucherCode(reportDto.getVoucherCode());
        //reportDto.setSubsidiary(subsidiaryRepository.findByIdentifier(reportDto.getSubId()));
        Site site = siteRepository.findByIdentifier(reportDto.getCurrentSite());
        if (site != null) {
            reportDto.setSites(List.of(site.getIdentifier()));
        }

        if (node != null) {
            Map<String, List<String>> headersAndParams = reportService.getHeaders(node, reportDto.getSubId(), reportDto.getMapping());
            List<String> columnNames = headersAndParams.get(P_NAMES);

            List<String> columnValues = headersAndParams.get(P_VALUES);

            reportWsDto.setColumnNames(columnNames);

            List<List<String>> results = new ArrayList<>();


            //Pageable pageable = getPageable(reportDto.getPage(), reportDto.getSizePerPage(), reportDto.getSortDirection(), reportDto.getSortField());
            //GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
            //List<BaseEntity> documents = genericRepository.getBySessionId(reportDto.getCurrentSessionId(), pageable);
            List<BaseEntity> documents = reportService.getReport(reportDto);
            List<Map<String, Object>> values = reportService.adjustReportForHeaders(documents, columnValues);
            for (Map<String, Object> result : values) {
                List<String> columnResult = new ArrayList<>();
                for (String key : columnValues) {
                    String value = String.valueOf(result.get(key));
                    if (value.contains("|https")) {
                        String[] splitVal = value.split("\\|");
                        value = "<a href='" + splitVal[1] + "' target='_blank'>" + splitVal[0] + "</a>";
                    }
                    columnResult.add(value);
                }
                results.add(columnResult);
            }
            reportWsDto.setData(results);
        }
        return reportWsDto;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public SourceTargetMappingDto getReport(@RequestBody @PathVariable(required = true) String id) throws IOException {
        SourceTargetMappingDto sourceTargetMappingDto = new SourceTargetMappingDto();
        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(id + "Mapping");
        if (null != mapping) {
            if (mapping.getIdentifier().equalsIgnoreCase("eup2CustomerValidationMapping")) {
                sourceTargetMappingDto.setEnableSkus(true);
            }
            sourceTargetMappingDto.setEnableVariant(mapping.getEnableVariant());
            sourceTargetMappingDto.setEnableVoucher(mapping.getEnableVoucher());
            sourceTargetMappingDto.setEnableCurrentPage(mapping.getEnableCurrentPage());
            sourceTargetMappingDto.setEnableCategory(mapping.getEnableCategory());
            sourceTargetMappingDto.setEnableToggle(mapping.getEnableToggle());
        }
        return sourceTargetMappingDto;
    }
}
