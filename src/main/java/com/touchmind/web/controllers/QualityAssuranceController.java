package com.touchmind.web.controllers;

import com.touchmind.core.HotFolderConstants;
import com.touchmind.core.mongo.dto.*;
import com.touchmind.core.mongo.model.*;
import com.touchmind.core.mongo.repository.*;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.*;
import com.touchmind.mail.service.MailService;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.utils.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.lang.System;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/qa")
public class QualityAssuranceController extends QaBaseController {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DASHBOARD = "dashboard";
    public static final String CURRENT_USER = "currentUser";
    private static final Logger LOG = LoggerFactory.getLogger(QualityAssuranceController.class);
    @Autowired
    private QARepository qaRepository;
    @Autowired
    private QualityAssuranceService qualityAssuranceService;
    @Autowired
    private EnvironmentRepository environmentRepository;
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestProfileRepository testProfileRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private org.springframework.core.env.Environment env;

    @Autowired
    private SiteService siteService;

    @Autowired
    private QaResultReportRepository qaResultReportRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private QaLocatorResultReportRepository qaLocatorResultReportRepository;

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

    @GetMapping(value = "/chart")
    @ResponseBody
    public String getTestCharts() {
        return "qa/qachart";
    }

    @GetMapping("/results")
    @ResponseBody
    public TestPlanWsDto getReportData(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        populateCommonData(testPlanWsDto);
        String profile = System.getProperty("spring.profiles.active");
        testPlanWsDto.setEnableBtn(true);
        if (StringUtils.isNotEmpty(profile) && profile.equalsIgnoreCase(DASHBOARD)) {
            testPlanWsDto.setEnableBtn(false);
        }
        return testPlanWsDto;
    }

    public void populateCommonData(TestPlanWsDto testPlanWsDto) {
        testPlanWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), List.class));
    }

    @GetMapping("/entryresults")
    @ResponseBody
    public TestPlanWsDto getReportDataEntry(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        populateCommonData(testPlanWsDto);
        String profile = System.getProperty("spring.profiles.active");
        testPlanWsDto.setEnableBtn(true);
        if (StringUtils.isNotEmpty(profile) && profile.equalsIgnoreCase(DASHBOARD)) {
            testPlanWsDto.setEnableBtn(false);
        }
        return testPlanWsDto;
    }

    @GetMapping("/subResults")
    @ResponseBody
    public TestPlanWsDto getReportResultData(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        populateCommonData(testPlanWsDto);
        testPlanWsDto.setSubsidiary(testPlanWsDto.getSubsidiary());
        return testPlanWsDto;
    }

    @RequestMapping(value = "/getTestPlanForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<TestPlanDto> getTestPlanForSubsidiary(@RequestBody TestPlanDto testPlanDto) {
        return modelMapper.map(testPlanService.findBySubsidiaryAndStatus(testPlanDto.getRecordId(), true), List.class);
    }

    @RequestMapping(value = "/getSitesForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody List<SiteDto> getSites(@RequestBody SiteDto siteDto) {
        return modelMapper.map(siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(siteDto.getRecordId(), true), List.class);
    }

    @RequestMapping(value = "/getEnvironmentsForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<EnvironmentDto> getEnvironmentsForSubsidiary(@RequestBody EnvironmentDto environmentDto) {
        return modelMapper.map(environmentRepository.findBySubsidiaries(environmentDto.getRecordId()), List.class);
    }

    @RequestMapping(value = "/getTestProfileForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<TestProfileDto> getTestProfileForSubsidiary(@RequestBody TestProfileDto testProfileDto) {
        return modelMapper.map(testProfileRepository.findBySubsidiaryOrderByIdentifier(testProfileDto.getRecordId(), true), List.class);
    }

    @PostMapping("/testPlans")
    @ResponseBody
    public TestPlanWsDto runTestPlan(@RequestBody TestPlanWsDto testPlanWsDto) {
        try {
            String params = objectMapper.writeValueAsString(testPlanWsDto);
            LOG.info("Populating test data with parameters: {}", params);
            qualityAssuranceService.runTest(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return testPlanWsDto;
    }

    @GetMapping(value = "/file_download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource downloadFile(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        try {
            String filename = testPlanWsDto.getFileName();
            String path = "src/main/resources/public/makingAPK/" + filename + "/" + filename + ".apk"; //path of your file
            return new FileSystemResource(new File(path));
        } catch (Exception e) {
            LOG.error("error in file_download " + e);
            return null;
        }
    }

    private List<MultiValuedMap<String, String>> populateErrorMap(String errorValue) {
        List<MultiValuedMap<String, String>> output = new ArrayList<>();
        errorValue = errorValue.substring(2, errorValue.length() - 2);

        String[] splitValues = errorValue.replace("},{", "!").replaceAll("}", "").split("!");
        for (String value : splitValues) {
            String[] splitVal = value.trim().split(",");
            MultiValuedMap<String, String> errorMap = new ArrayListValuedHashMap<>();
            for (String val : splitVal) {
                String[] keyVal = val.trim().split("=");
                String key = keyVal[0];
                if (StringUtils.isNotEmpty(key) && !key.equalsIgnoreCase("null") && keyVal.length > 1) {
                    if (StringUtils.isNotEmpty(keyVal[1]) && !keyVal[1].equalsIgnoreCase("null")) {
                        errorMap.put(keyVal[0], keyVal[1]);
                    }
                }
            }
            output.add(errorMap);
        }
        return output;
    }

    @PostMapping("/sendMail")
    @ResponseBody
    public TestPlanWsDto sendMail(@RequestBody TestPlanWsDto testPlanWsDto) {
        CronJobProfile cronjobProfile = cronJobProfileRepository.findByRecordId(testPlanWsDto.getTestPlans().get(0).getRecordId());
        List<QATestResult> qaTestResults = new ArrayList<>();
        for (TestPlanDto testPlanDto : testPlanWsDto.getTestPlans()) {
            QATestResult qaRepositoryOp = null;
            //TODO
            //QATestResult qaRepositoryOp = qaRepository.findByRecordId(testPlanDto.getRecordId());
            if (qaRepositoryOp != null) {
                qaTestResults.add(qaRepositoryOp);
            }
        }
        if (cronjobProfile != null) {
            mailService.sendMailBulkResults(testPlanWsDto.getEmailSubject(), cronjobProfile.getRecipients(), qaTestResults);
        }
        testPlanWsDto.setMessage("Mail Sent");
        return testPlanWsDto;
    }

    /*@RequestMapping(value = "/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    @ResponseBody
    public String processSubsidiaryReportJson(@RequestBody TestPlanWsDto testPlanDto) {
        Pageable pageable = getPageable(testPlanDto.getPage(), testPlanDto.getSizePerPage(), testPlanDto.getSortDirection(), testPlanDto.getSortField());

        Subsidiary subsidiaryObject = subsidiaryRepository.findByIdentifier(testPlanDto.getSubsidiary());
        String subsidiaryId = null;
        if (subsidiaryObject != null) {
            subsidiaryId = String.valueOf(subsidiaryObject.getId());
        }

        if (StringUtils.isNotEmpty(fieldNameAndFieldValue.getFieldName())) {
            page = qaResultsRepository.findByFields(fieldNameAndFieldValue, subsidiaryId, pageable);
        } else {
            page = qaResultsRepository.findAllBySubsidiary(subsidiaryId, pageable);
        }

        return gson.toJson(table);
    }*/

    @PostMapping(value = "/results")
    @ResponseBody
    public QATestResultWsDto QATestResultDto(@RequestBody QATestResultWsDto qaTestResultWsDto) {
        Pageable pageable = getPageable(qaTestResultWsDto.getPage(), qaTestResultWsDto.getSizePerPage(), qaTestResultWsDto.getSortDirection(), qaTestResultWsDto.getSortField());
        QATestResultDto qaTestResultDto = CollectionUtils.isNotEmpty(qaTestResultWsDto.getQaTestResults()) ? qaTestResultWsDto.getQaTestResults().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(qaTestResultDto, qaTestResultWsDto.getOperator());
        QATestResult qaTestResult = qaTestResultDto != null ? modelMapper.map(qaTestResultDto, QATestResult.class) : null;
        Page<QATestResult> page = isSearchActive(qaTestResult) != null ? qaRepository.findAll(Example.of(qaTestResult, exampleMatcher), pageable) : qaRepository.findAll(pageable);
        qaTestResultWsDto.setQaTestResults(modelMapper.map(page.getContent(), List.class));
        qaTestResultWsDto.setBaseUrl("/qa/results");
        qaTestResultWsDto.setTotalPages(page.getTotalPages());
        qaTestResultWsDto.setTotalRecords(page.getTotalElements());
        qaTestResultWsDto.setAttributeList(getConfiguredAttributes(qaTestResultWsDto.getNode()));
        return qaTestResultWsDto;
    }

    @PostMapping(value = "/resultReport")
    @ResponseBody
    public QALocatorResultReportWsDto qaLocatorResultReportDto(@RequestBody QALocatorResultReportWsDto qaLocatorResultReportWsDto) {
        Pageable pageable = getPageable(qaLocatorResultReportWsDto.getPage(), qaLocatorResultReportWsDto.getSizePerPage(), qaLocatorResultReportWsDto.getSortDirection(), qaLocatorResultReportWsDto.getSortField());
        QALocatorResultReportDto qaLocatorResultReportDto = CollectionUtils.isNotEmpty(qaLocatorResultReportWsDto.getLocatorResultReportDtoList()) ? qaLocatorResultReportWsDto.getLocatorResultReportDtoList().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(qaLocatorResultReportDto, qaLocatorResultReportWsDto.getOperator());
        QaLocatorResultReport qaLocatorResultReport = qaLocatorResultReportDto != null ? modelMapper.map(qaLocatorResultReportDto, QaLocatorResultReport.class) : null;
        Page<QaLocatorResultReport> page = isSearchActive(qaLocatorResultReport) != null ? qaLocatorResultReportRepository.findAll(Example.of(qaLocatorResultReport, exampleMatcher), pageable) : qaLocatorResultReportRepository.findAll(pageable);
        qaLocatorResultReportWsDto.setLocatorResultReportDtoList(modelMapper.map(page.getContent(), List.class));
        qaLocatorResultReportWsDto.setBaseUrl("/qa/resultReport");
        qaLocatorResultReportWsDto.setTotalPages(page.getTotalPages());
        qaLocatorResultReportWsDto.setTotalRecords(page.getTotalElements());
        qaLocatorResultReportWsDto.setAttributeList(getConfiguredAttributes(qaLocatorResultReportWsDto.getNode()));
        return qaLocatorResultReportWsDto;
    }

    @RequestMapping(value = "/qaResults", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    @ResponseBody
    public String qaResults(@RequestParam(value = "start", required = false, defaultValue = "0") Integer start, @RequestParam(value = "length", required = false, defaultValue = "100") Integer length,
                            @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "testName", required = false) String testName) {
        Pageable pageable = getPageable(start / length, length, Sort.Direction.DESC, "id");
        Page<QATestResult> page = getQaTestResults(startDate, endDate, testName, pageable);
        return gson.toJson(page.getContent());
    }

    @RequestMapping(value = "/reports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    @ResponseBody
    public String processApiReport(@RequestParam(value = "start", required = false, defaultValue = "0") Integer start, @RequestParam(value = "length", required = false, defaultValue = "100") Integer length, @RequestParam(value = "startDate", required = false) String startDate,
                                   @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "testCaseId", required = false) String testCaseId) {
        Pageable pageable = getPageable(start / length, length, Sort.Direction.DESC, "id");
        Page<QaResultReport> page = getQaResultReports(startDate, endDate, testCaseId, pageable);

        DataTable<List<QaResultReport>> table = new DataTable<>();
        table.setRecordsTotal(page.getTotalElements());
        table.setRecordsFiltered(page.getTotalElements());
        table.setStart(page.getPageable().getPageNumber());
        table.setLength(page.getSize());
        table.setContent(page.getContent());
        return gson.toJson(table);
    }

    private Page<QaResultReport> getQaResultReports(String startDate, String endDate, String testCaseId, Pageable pageable) {
        Page<QaResultReport> page;
        if (StringUtils.isNotEmpty(startDate) || StringUtils.isNotEmpty(endDate)) {
            if (StringUtils.isEmpty(endDate)) {
                endDate = startDate;
            }
            try {
                Date dateFrom = df.parse(startDate);
                Date dateTo = df.parse(endDate);
                if (StringUtils.isNotEmpty(testCaseId)) {
                    page = qaResultReportRepository.findByCreationTimeBetweenAndTestCaseIdIgnoreCaseLike(dateFrom, dateTo, testCaseId, pageable);
                } else {
                    page = qaResultReportRepository.findByCreationTimeBetween(dateFrom, dateTo, pageable);
                }
            } catch (Exception e) {
                page = qaResultReportRepository.findAll(pageable);
            }
        } else {
            if (StringUtils.isNotEmpty(testCaseId)) {
                page = qaResultReportRepository.findByTestCaseIdIgnoreCaseLike(testCaseId, pageable);
            } else {
                page = qaResultReportRepository.findAll(pageable);
            }
        }
        return page;
    }

    private Page<QATestResult> getQaTestResults(String startDate, String endDate, String testName, Pageable pageable) {
        Page<QATestResult> page;
        if (StringUtils.isNotEmpty(startDate) || StringUtils.isNotEmpty(endDate)) {
            if (StringUtils.isEmpty(endDate)) {
                endDate = startDate;
            }
            try {
                Date dateFrom = df.parse(startDate);
                Date dateTo = df.parse(endDate);
                if (StringUtils.isNotEmpty(testName)) {
                    page = qaRepository.findByCreationTimeBetweenAndTestNameIgnoreCaseLike(dateFrom, dateTo, testName, pageable);
                } else {
                    page = qaRepository.findByCreationTimeBetween(dateFrom, dateTo, pageable);
                }
            } catch (Exception e) {
                page = qaRepository.findAll(pageable);
            }
        } else {
            if (StringUtils.isNotEmpty(testName)) {
                page = qaRepository.findByTestNameIgnoreCaseLike(testName, pageable);
            } else {
                page = qaRepository.findAll(pageable);
            }
        }
        return page;
    }
}
