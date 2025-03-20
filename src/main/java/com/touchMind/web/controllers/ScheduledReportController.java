package com.touchMind.web.controllers;

import com.touchMind.core.HotFolderConstants;
import com.touchMind.core.mongo.dto.CronHistoryDto;
import com.touchMind.core.mongo.dto.CronHistoryWsDto;
import com.touchMind.core.mongo.dto.ReportCompilerMappingDto;
import com.touchMind.core.mongo.dto.ReportDto;
import com.touchMind.core.mongo.dto.ReportWsDto;
import com.touchMind.core.mongo.model.CronHistory;
import com.touchMind.core.mongo.model.DataSource;
import com.touchMind.core.mongo.model.Node;
import com.touchMind.core.mongo.model.ReportCompiler;
import com.touchMind.core.mongo.model.SchedulerJob;
import com.touchMind.core.mongo.model.SourceTargetMapping;
import com.touchMind.core.mongo.model.baseEntity.BaseEntity;
import com.touchMind.core.mongo.repository.CronHistoryRepository;
import com.touchMind.core.mongo.repository.DataSourceRepository;
import com.touchMind.core.mongo.repository.ReportCompilerRepository;
import com.touchMind.core.mongo.repository.SchedulerJobRepository;
import com.touchMind.core.mongo.repository.SourceTargetMappingRepository;
import com.touchMind.core.mongo.repository.generic.GenericRepository;
import com.touchMind.core.service.RepositoryService;
import com.touchMind.tookit.service.ReportService;
import com.touchMind.web.controllers.toolkit.BaseController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/schedule")
public class ScheduledReportController extends BaseController {

    public static final String SCHEDULE_PATH = "/schedule/";

    public static final String COMPILE_PATH = "compile/";

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledReportController.class);
    @Autowired
    SchedulerJobRepository schedulerJobRepository;
    @Autowired
    ReportCompilerRepository reportCompilerRepository;
    @Autowired
    private CronHistoryRepository cronHistoryRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SourceTargetMappingRepository sourceTargetMappingRepository;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DataSourceRepository dataSourceRepository;

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[in.available()];
        int len;
        // read bytes from the input stream and store them in buffer
        while ((len = in.read(buffer)) != -1) {
            // write bytes from the buffer into output stream
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    @PostMapping("/summary")
    @ResponseBody
    public CronHistoryWsDto listSchedules(@RequestBody CronHistoryWsDto cronHistoryWsDto) throws JsonProcessingException {
        Pageable pageable = getPageable(cronHistoryWsDto.getPage(), cronHistoryWsDto.getSizePerPage(), cronHistoryWsDto.getSortDirection(), cronHistoryWsDto.getSortField());
        CronHistoryDto cronHistoryDto = CollectionUtils.isNotEmpty(cronHistoryWsDto.getCronHistories()) ? cronHistoryWsDto.getCronHistories().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(cronHistoryDto, false, cronHistoryWsDto.getOperator());
        CronHistory cronHistory = cronHistoryDto != null ? modelMapper.map(cronHistoryDto, CronHistory.class) : null;
        Page<CronHistory> page = isSearchActive(cronHistory) != null ? cronHistoryRepository.findAll(Example.of(cronHistory, exampleMatcher), pageable) : cronHistoryRepository.findAll(pageable);
        Type listType = new TypeToken<List<CronHistoryDto>>() {
        }.getType();
        cronHistoryWsDto.setCronHistories(modelMapper.map(page.getContent(), listType));
        cronHistoryWsDto.setTotalPages(page.getTotalPages());
        cronHistoryWsDto.setTotalRecords(page.getTotalElements());
        return cronHistoryWsDto;
    }

    /*@GetMapping("/ajax/{id}")
    @ResponseBody
    public String processReportJson(@PathVariable(required = true) String id, @RequestParam("draw") Integer draw, @RequestParam Map<String, String> allRequestParams,
                                    @RequestParam("start") Integer start, @RequestParam("length") Integer length, Model model) {

        SchedulerJob schedule = schedulerJobRepository.findByNodePath(SCHEDULE_PATH + id);
        List<BaseEntity> filteredDocuments = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();
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
            List<BaseEntity> pagedRecords = new ArrayList<>();
            //TODO Check if this is correctly fetching the data
            GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getRecordId());
            if (length > 0) {
                Pageable paging = PageRequest.of(start / length, length);
                pagedRecords = genericRepository.getBySessionId(currentUserSession, paging);
            }
            List<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession);
            Map<String, List<String>> headersAndParams = getReportService().getHeaders(node, schedule.getSubsidiary(), schedule.getMapping());
            model.addAttribute(P_VALUES, headersAndParams.get(P_VALUES));
            model.addAttribute(P_NAMES, headersAndParams.get(P_NAMES));

            if (CollectionUtils.isNotEmpty(pagedRecords)) {
                BaseEntity entity = pagedRecords.get(0);
                if (entity instanceof PriceDocuments && currentUserSession.contains("%_")) {
                    String[] parts = currentUserSession.split("%_");
                    if (parts.length > 0) {
                        double discount = Double.valueOf(parts[0]);
                        for (BaseEntity document : pagedRecords) {
                            Map<String, Object> records = document.getRecords();
                            if (records.containsKey(PRODUCT_DISCOUNT_VALUE) && Double.valueOf(String.valueOf(records.get(PRODUCT_DISCOUNT_VALUE)).replaceAll(
                                    "%", "")) >= discount && discount >= Double.valueOf(pricePercent)) {
                                filteredDocuments.add(document);
                            }
                        }
                    }
                } else {
                    filteredDocuments.addAll(pagedRecords);
                }
            }
            if (pagedRecords.size() > filteredDocuments.size()) {
                results.addAll(getReportService().adjustReportForHeaders(filteredDocuments, (List<String>) model.getAttribute(P_VALUES)));
            } else {
                results.addAll(getReportService().adjustReportForHeaders(allRecords, (List<String>) model.getAttribute(P_VALUES)));
            }
        }
        JSONObject jsonObject = getJsonFormattedData(draw, model, filteredDocuments, results, allRequestParams);
        return jsonObject.toString();
    }*/

    @PostMapping("/{id}")
    @ResponseBody
    public ReportWsDto processReport(@RequestBody ReportDto reportDto) {
        ReportWsDto reportWsDto = new ReportWsDto();
        reportDto.setBaseUrl(reportDto.getNodePath());
        Pageable pageable = getPageable(reportDto.getPage(), reportDto.getSizePerPage(), reportDto.getSortDirection(), reportDto.getSortField());
        Node node = nodeRepository.findByPath(reportDto.getNodePath());
        Optional.ofNullable(node).ifPresent(n -> {
            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByNode(n.getIdentifier());
            Optional.ofNullable(sourceTargetMapping).ifPresent(mapping -> {
                if (CollectionUtils.isNotEmpty(mapping.getSubsidiaries())) {
                    Map<String, List<String>> headersAndParams = getReportService().getHeaders(n, mapping.getSubsidiaries().getFirst(), mapping.getIdentifier());
                    List<String> columnValues = headersAndParams.get(P_VALUES);
                    reportWsDto.setColumnNames(headersAndParams.get(P_NAMES));

                    List<List<String>> results = new ArrayList<>();

                    DataSource dataSource = (CollectionUtils.isNotEmpty(mapping.getSourceTargetParamMappings()) && Objects.nonNull(mapping.getSourceTargetParamMappings().getFirst().getDataSource()))
                            ? dataSourceRepository.findByIdentifier(mapping.getSourceTargetParamMappings().getFirst().getDataSource()) : null;
                    Optional.ofNullable(dataSource).ifPresent(ds -> {
                        String currentUserSession = ds.getIdentifier();
                        GenericRepository<BaseEntity> genericRepository = repositoryService.getRepositoryForNodeId(n.getIdentifier());
                        Page<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession, pageable);
                        List<Map<String, Object>> matchedRecords = reportService.adjustReportForHeaders(allRecords.getContent(), columnValues);
                        reportWsDto.setTotalPages(allRecords.getTotalPages());
                        reportWsDto.setTotalRecords(allRecords.getTotalElements());
                        matchedRecords.forEach(result -> {
                            List<String> columnResult = new ArrayList<>();
                            columnValues.forEach(key -> {
                                columnResult.add(String.valueOf(result.get(key)));
                            });
                            results.add(columnResult);
                        });
                    });
                    reportWsDto.setData(results);
                }
            });
        });
        return reportWsDto;
    }

    @GetMapping("/compile/{id}")
    @ResponseBody
    public String processCompileReport(@PathVariable(required = true) String id, Model model) {

        Node reportNode = nodeRepository.findByPath(SCHEDULE_PATH + COMPILE_PATH + id);
        ReportCompiler reportCompiler = reportCompilerRepository.findByNode(String.valueOf(reportNode.getId()));
        List<String> values = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        String key = StringUtils.EMPTY;
        String dynamicKey = StringUtils.EMPTY;
        Map<String, Map<String, Integer>> resultsMap = new HashMap();
        List<Map<String, Object>> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportCompiler.getReportCompilerMappings())) {
            for (ReportCompilerMappingDto mapping : reportCompiler.getReportCompilerMappings()) {
                if (mapping.getHeader().equalsIgnoreCase("ROW")) {
                    key = mapping.getParam();
                }
                if (mapping.getHeader().contains("HEADER")) {
                    headers.add(mapping.getParam());
                }
                values.add(mapping.getParam());
            }
        }

        populateApiResults(id, reportCompiler, values, results);
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> result : results) {
            for (String resultKey : result.keySet()) {
                String mapValue = String.valueOf(result.get(resultKey));
                if (!resultKey.equalsIgnoreCase(key) && !headers.contains(resultKey)) {
                    if (resultMap.containsKey(mapValue)) {
                        int count = resultMap.get(mapValue);
                        resultMap.put(mapValue, count + 1);
                    } else {
                        resultMap.put(mapValue, 1);
                    }
                } else {
                    if (resultsMap.containsKey(mapValue)) {
                        resultMap.putAll(resultsMap.get(mapValue));
                    } else {
                        resultMap = new HashMap<>();
                    }
                    if (CollectionUtils.isNotEmpty(headers)) {
                        dynamicKey = String.valueOf(result.get(key));
                        for (String header : headers) {
                            Object value = result.get(header);
                            if (Objects.nonNull(value)) {
                                dynamicKey = dynamicKey + "§" + value;
                            }
                        }
                        resultsMap.put(dynamicKey, resultMap);
                    } else {
                        resultsMap.put(String.valueOf(result.get(key)), resultMap);
                    }
                }
            }
        }

        List<String> staticColumns = new ArrayList<>();
        staticColumns.add(key);
        staticColumns.addAll(headers);

        Set<String> columnNames = new HashSet<>();
        for (Map<String, Integer> column : resultsMap.values()) {
            columnNames.addAll(column.keySet());
        }

        model.addAttribute("url", SCHEDULE_PATH + COMPILE_PATH + "download/" + id);
        model.addAttribute("columnNames", columnNames);
        model.addAttribute("staticColumns", staticColumns);
        model.addAttribute("resultsMap", resultsMap);
        return "scheduler/reportContent-compile";
    }

    @PostMapping(value = "/compile/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> downloadCompileReport(@PathVariable(required = true) String id) {

        try {
            Node reportNode = nodeRepository.findByPath(SCHEDULE_PATH + COMPILE_PATH + id);
            ReportCompiler reportCompiler = reportCompilerRepository.findByNode(String.valueOf(reportNode.getId()));
            List<String> values = new ArrayList<>();

            List<Map<String, Object>> results = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(reportCompiler.getReportCompilerMappings())) {
                for (ReportCompilerMappingDto mapping : reportCompiler.getReportCompilerMappings()) {
                    values.add(mapping.getParam());
                }
            }

            populateApiResults(id, reportCompiler, values, results);
            String currentTimeInMills = String.valueOf(System.currentTimeMillis());
            String fileName = currentTimeInMills + "_" + id + ".xlsx";
            Path path = Paths.get(HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION + "/" + fileName);
            File file = path.toFile();
            writeDataToExcel(file, results);
            byte[] skuData = toByteArray(new FileInputStream(file));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(skuData.length);
            return ResponseEntity.ok().headers(httpHeaders).body(skuData);
        } catch (Exception e) {
            LOG.error("error in file_download " + e);
        }
        return null;
    }

    public void writeDataToExcel(File file, List<Map<String, Object>> results) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");
        FileOutputStream out = null;
        int rowIndex = 0;
        for (Map<String, Object> dataMap : results) {
            Row row = sheet.createRow(rowIndex);
            int columnIndex = 0;
            for (String key : dataMap.keySet()) {
                Cell cell = row.createCell(columnIndex);
                cell.setCellValue(String.valueOf(dataMap.get(key)));
                columnIndex++;
            }
            rowIndex++;
            out = new FileOutputStream(
                    file);
            workbook.write(out);
        }
        out.close();
    }

    private void populateApiResults(String id, ReportCompiler reportCompiler, List<String> values, List<Map<String, Object>> results) {
        if (CollectionUtils.isNotEmpty(reportCompiler.getReportInterfaces())) {
            for (String interfacePath : reportCompiler.getReportInterfaces()) {
                Node interfaceNode = nodeRepository.findByPath(interfacePath);
                if (null != interfaceNode) {
                    SchedulerJob schedule = schedulerJobRepository.findByNodePath(interfaceNode.getPath());
                    if (schedule != null) {
                        Node node = nodeRepository.findByPath(TOOLKIT + id);
                        if (null == node) {
                            SourceTargetMapping sourceTargetMapping = sourceTargetMappingRepository.findByIdentifier(schedule.getMapping());
                            if (null != sourceTargetMapping) {
                                //TODO Check if this is correctly fetching the data
                                Node nodeOptional = nodeRepository.findByIdentifier(sourceTargetMapping.getNode());
                                if (nodeOptional != null) {
                                    node = nodeOptional;
                                }
                            }
                        }

                        String currentUserSession = schedule.getCronId();
                        //TODO Check if this is correctly fetching the data
                        GenericRepository genericRepository = repositoryService.getRepositoryForNodeId(node.getIdentifier());
                        List<BaseEntity> allRecords = genericRepository.getBySessionId(currentUserSession);

                        results.addAll(getReportService().adjustReportForHeaders(allRecords, values));
                    }
                }
            }
        }
    }
}