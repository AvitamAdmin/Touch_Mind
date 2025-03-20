//package com.touchMind.web.controllers;
//
//import com.touchMind.core.mongo.dto.ReportDto;
//import com.touchMind.core.mongo.dto.ReportWsDto;
//import com.touchMind.core.mongo.model.DataSource;
//import com.touchMind.core.mongo.model.Node;
//import com.touchMind.core.mongo.model.SourceTargetMapping;
//import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
//import com.touchMind.core.mongo.repository.DataSourceRepository;
//import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
//import com.touchMind.core.mongo.repository.generic.GenericRepository;
//import com.touchMind.core.service.RepositoryService;
//import com.touchMind.tookit.service.ReportService;
//import com.touchMind.web.controllers.toolkit.BaseController;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//
//
//@RestController
//@RequestMapping("/orderMonitor")
//public class OrderMonitorController extends BaseController {
//
//    @Autowired
//    ReportService reportService;
//    @Autowired
//    private RepositoryService repositoryService;
//    @Autowired
//    private SourceTargetMappingRepository sourceTargetMappingRepository;
//
//    @Autowired
//    private DataSourceRepository dataSourceRepository;
//
//    @PostMapping("/{id}")
//    @ResponseBody
//    public ReportWsDto processReport(@RequestBody ReportDto reportDto) {
//        ReportWsDto reportWsDto = new ReportWsDto();
//        reportDto.setBaseUrl(reportDto.getNodePath());
//        Pageable pageable = getPageable(reportDto.getPage(), reportDto.getSizePerPage(), reportDto.getSortDirection(), reportDto.getSortField());
//        Node node = nodeRepository.findByPath(reportDto.getNodePath());
//        Optional.ofNullable(node).ifPresent(n -> {
//            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByNode(n.getIdentifier());
//            Optional.ofNullable(sourceTargetMapping).ifPresent(mapping -> {
//                if (CollectionUtils.isNotEmpty(mapping.getSubsidiaries())) {
//                    Map<String, List<String>> headersAndParams = getReportService().getHeaders(n, mapping.getSubsidiaries().getFirst(), mapping.getIdentifier());
//                    List<String> columnValues = headersAndParams.get(P_VALUES);
//                    reportWsDto.setColumnNames(headersAndParams.get(P_NAMES));
//
//                    List<List<String>> results = new ArrayList<>();
//
//                    DataSource dataSource = (CollectionUtils.isNotEmpty(mapping.getSourceTargetParamMappings()) && Objects.nonNull(mapping.getSourceTargetParamMappings().getFirst().getDataSource()))
//                            ? dataSourceRepository.findByIdentifier(mapping.getSourceTargetParamMappings().getFirst().getDataSource()) : null;
//                    Optional.ofNullable(dataSource).ifPresent(ds -> {
//                        String currentUserSession = ds.getIdentifier();
//                        GenericRepository<BaseEntity> genericRepository = repositoryService.getRepositoryForNodeId(n.getIdentifier());
//                        Page<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession, pageable);
//                      //  List<Map<String, Object>> matchedRecords = reportService.adjustReportForHeaders(allRecords.getContent(), columnValues);
//                        reportWsDto.setTotalPages(allRecords.getTotalPages());
//                        reportWsDto.setTotalRecords(allRecords.getTotalElements());
//                       // matchedRecords.forEach(result -> {
//                            List<String> columnResult = new ArrayList<>();
//                            columnValues.forEach(key -> {
//                         //       columnResult.add(String.valueOf(result.get(key)));
//                            });
//                            results.add(columnResult);
//                        });
//                    });
//                    reportWsDto.setData(results);
//                }
//            });
//        });
//        //return reportWsDto;
//    }
//
//    /*@GetMapping("/ajax/{id}")
//    @ResponseBody
//    public String processReportJson(@PathVariable(required = true) String id, @RequestParam("draw") Integer draw, @RequestParam("start") Integer start,
//                                    @RequestParam("length") Integer length, @RequestParam Map<String, String> allRequestParams,
//                                    Model model) {
//
//        SourceTargetMapping mapping = sourceTargetMappingRepository.findByIdentifier(id + "Mapping");
//        //TODO Check if this is correctly fetching the data
//        Node node = nodeRepository.findByIdentifier(mapping.getNode());
//        DataSource dataSource = dataSourceRepository.findByIdentifier(mapping.getSourceTargetParamMappings().get(0).getDataSource());
//        String currentUserSession = dataSource.getIdentifier();
//        //TODO Check if this is correctly fetching the data
//        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getIdentifier());
//        List<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession);
//        Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, mapping.getSubsidiaries().get(0), id + "Mapping");
//        model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
//        model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));
//        List<Map<String, Object>> results = new ArrayList<>();
//        if (length > 0) {
//            Pageable paging = PageRequest.of(start / length, length);
//            List<BaseEntity> documents = genericRepository.getBySessionId(currentUserSession, paging);
//            results = reportService.adjustReportForHeaders(documents, (List<String>) model.getAttribute(P_VALUES));
//        } else {
//            results = reportService.adjustReportForHeaders(allRecords, (List<String>) model.getAttribute(P_VALUES));
//        }
//
//        JSONObject jsonObject = getJsonFormattedData(draw, model, allRecords, results, allRequestParams);
//        return jsonObject.toString();
//    }*/
//}