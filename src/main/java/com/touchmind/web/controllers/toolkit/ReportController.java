package com.touchmind.web.controllers.toolkit;

import com.touchmind.core.mongo.dto.ReportDto;
import com.touchmind.core.mongo.dto.ReportWsDto;
import com.touchmind.core.mongo.dto.SourceTargetMappingDto;
import com.touchmind.core.mongo.model.Node;
import com.touchmind.core.mongo.model.SourceTargetMapping;
import com.touchmind.core.mongo.model.baseEntity.BaseEntity;
import com.touchmind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchmind.core.mongo.repository.generic.GenericRepository;
import com.touchmind.core.service.RepositoryService;
import com.touchmind.tookit.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @PostMapping(value = "/reportResult")
    @ResponseBody
    public ReportWsDto processReportJson(@RequestBody ReportDto reportDto, HttpServletRequest request) {
        ReportWsDto reportWsDto = new ReportWsDto();
        String currentUserSession = reportService.getCurrentUserSessionId(request, reportDto.getRecordId());
        Node node = nodeRepository.findByPath(TOOLKIT + reportDto.getRecordId());
        reportDto.setCurrentSessionId(currentUserSession);
        reportDto.setMapping(reportDto.getRecordId() + "Mapping");
        if (null == node) {
            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(reportDto.getMapping());
            if (null != sourceTargetMapping) {
                Node nodeOptional = nodeRepository.findByRecordId(sourceTargetMapping.getNode());
                if (nodeOptional != null) {
                    node = nodeOptional;
                }
            }
        }
        reportDto.setCurrentNode(node != null ? node.getRecordId() : null);
        reportDto.setVoucherCode(reportDto.getVoucherCode());

        Map<String, List<String>> headersAndParams = reportService.getHeaders(node, reportDto.getSubId(), reportDto.getMapping());
        reportWsDto.setColumnNames(headersAndParams.get(P_NAMES));

        List<String> columnValues = headersAndParams.get(P_VALUES);

        List<Map<String, Object>> results = new ArrayList<>();
        Pageable pageable = getPageable(reportDto.getPage(), reportDto.getSizePerPage(), reportDto.getSortDirection(), reportDto.getSortField());
        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(reportDto.getCurrentNode());
        List<BaseEntity> documents = genericRepository.getBySessionId(reportDto.getCurrentSessionId(), pageable);
        results.addAll(reportService.adjustReportForHeaders(documents, columnValues));
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
