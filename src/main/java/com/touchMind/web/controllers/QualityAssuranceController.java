package com.touchMind.web.controllers;

import com.touchMind.core.HotFolderConstants;
import com.touchMind.core.mongo.dto.CategoryDto;
import com.touchMind.core.mongo.dto.CronJobProfileDto;
import com.touchMind.core.mongo.dto.EmailDto;
import com.touchMind.core.mongo.dto.EmailWsDto;
import com.touchMind.core.mongo.dto.EnvironmentDto;
import com.touchMind.core.mongo.dto.QALocatorResultReportDto;
import com.touchMind.core.mongo.dto.QALocatorResultReportWsDto;
import com.touchMind.core.mongo.dto.QATestResultDto;
import com.touchMind.core.mongo.dto.QATestResultWsDto;
import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.SiteDto;
import com.touchMind.core.mongo.dto.TestPlanDto;
import com.touchMind.core.mongo.dto.TestPlanWsDto;
import com.touchMind.core.mongo.dto.TestProfileDto;
import com.touchMind.core.mongo.model.CronJobProfile;
import com.touchMind.core.mongo.model.Model;
import com.touchMind.core.mongo.model.QaLocatorResultReport;
import com.touchMind.core.mongo.model.QaResultReport;
import com.touchMind.core.mongo.repository.CategoryRepository;
import com.touchMind.core.mongo.repository.CronJobProfileRepository;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.EnvironmentRepository;
import com.touchMind.core.mongo.repository.ErrorTypeRepository;
import com.touchMind.core.mongo.repository.QaLocatorResultReportRepository;
import com.touchMind.core.mongo.repository.QaResultReportRepository;
import com.touchMind.core.mongo.repository.QaTestPlanRepository;
import com.touchMind.core.mongo.repository.SiteRepository;
import com.touchMind.core.mongo.repository.TestProfileRepository;
import com.touchMind.core.mongotemplate.QATestResult;
import com.touchMind.core.mongotemplate.repository.QARepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.ExcelFileService;
import com.touchMind.core.service.FileService;
import com.touchMind.core.service.SiteService;
import com.touchMind.core.service.TestPlanService;
import com.touchMind.form.QATestResultForm;
import com.touchMind.mail.service.MailService;
import com.touchMind.qa.service.QualityAssuranceService;
import com.touchMind.utils.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/qa")
public class QualityAssuranceController extends QaBaseController {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DASHBOARD = "dashboard";
    public static final String CURRENT_USER = "currentUser";
    private static final Logger LOG = LoggerFactory.getLogger(QualityAssuranceController.class);
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private QARepository qaRepository;
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
//    @Autowired
//    private SubsidiaryService subsidiaryService;
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
    private FileService fileService;
    @Autowired
    private BaseService baseService;

    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;

    @Autowired
    private Gson gson;

    @Autowired
    private ExcelFileService excelFileService;
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
    private ErrorTypeRepository errorTypeRepository;

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
        Type listType = new TypeToken<List<CronJobProfileDto>>() {
        }.getType();
        testPlanWsDto.setCronJobProfiles(modelMapper.map(cronJobProfileRepository.findByStatusOrderByIdentifier(true), listType));
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
        Type listType = new TypeToken<List<TestPlanDto>>() {
        }.getType();
        return modelMapper.map(testPlanService.findBySubsidiaryAndStatus(testPlanDto.getIdentifier(), true), listType);
    }

    @RequestMapping(value = "/getSitesForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody List<SiteDto> getSites(@RequestBody SiteDto siteDto) {
        Type listType = new TypeToken<List<SiteDto>>() {
        }.getType();
        return modelMapper.map(siteRepository.findBySubsidiaryAndStatusOrderByIdentifier(siteDto.getIdentifier(), true), listType);
    }

    @RequestMapping(value = "/getEnvironmentsForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<EnvironmentDto> getEnvironmentsForSubsidiary(@RequestBody EnvironmentDto environmentDto) {
        Type listType = new TypeToken<List<EnvironmentDto>>() {
        }.getType();
        return modelMapper.map(environmentRepository.findBySubsidiaries(environmentDto.getIdentifier()), listType);
    }

    @RequestMapping(value = "/getTestProfileForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<TestProfileDto> getTestProfileForSubsidiary(@RequestBody TestProfileDto testProfileDto) {
        Type listType = new TypeToken<List<TestProfileDto>>() {
        }.getType();
        return modelMapper.map(testProfileRepository.findBySubsidiaryOrderByIdentifier(testProfileDto.getIdentifier(), true), listType);
    }

    @RequestMapping(value = "/getCategoryForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<CategoryDto> getCategoryForSubsidiary(@RequestBody CategoryDto categoryDto) {
        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(categoryRepository.findByStatusAndSubsidiariesOrderByIdentifier(true, categoryDto.getSubsidiaries()), listType);
    }

    @PostMapping("/testPlans")
    @ResponseBody
    public TestPlanWsDto runTestPlan(@RequestBody TestPlanWsDto testPlanWsDto) {
        try {
            testPlanWsDto.setCurrentUser(qaTestPlanRepository.findByIdentifier(testPlanWsDto.getTestPlan()).getIdentifier());
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

    @GetMapping(value = "/failedSkus", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> failedSkus(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        try {
            //TODO
            QATestResult testResults = null;
            //QATestResult testResults = qaRepository.findByIdentifier(testPlanWsDto.getTestPlans().get(0).getIdentifier());
            if (testResults != null) {
                String currentTimeInMills = String.valueOf(System.currentTimeMillis());
                String fileName = currentTimeInMills + "_" + testResults.getTestName() + "_failedSKUs.xlsx";
                Path path = Paths.get(HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION + "/" + fileName);
                File file = path.toFile();
                Map<String, String> failedErrorMap = testResults.getFailedSkusError();
                MultiValuedMap multiValuedMap = new ArrayListValuedHashMap(failedErrorMap);
                if (failedErrorMap != null && !failedErrorMap.isEmpty()) {
                    excelFileService.writeDataToExcel(file, List.of(multiValuedMap));
                }
                byte[] skuData = CommonUtil.toByteArray(new FileInputStream(file));
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
                httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                httpHeaders.setContentLength(skuData.length);
                return ResponseEntity.ok().headers(httpHeaders).body(skuData);
            }
        } catch (Exception e) {

            LOG.error("error in file_download " + e);
            return null;
        }
        return null;
    }

    @PostMapping(value = "/downloadFailedSkus", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> failedSkusData(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        try {
            String currentTimeInMills = String.valueOf(System.currentTimeMillis());
            String fileName = currentTimeInMills + "_failedSKUs.xlsx";
            Path path = Paths.get(HotFolderConstants.DEFAULT_HOT_FOLDER_LOCATION + "/" + fileName);
            File file = path.toFile();
            //String errorValue = httpServletRequest.getParameter("skuErrorMapList").replaceAll(", \\{", ",").replaceAll(", ", "");
            List<MultiValuedMap<String, String>> failedErrorMap = populateErrorMap(null);
            if (CollectionUtils.isNotEmpty(failedErrorMap)) {
                excelFileService.writeDataToExcel(file, failedErrorMap);
            }
            byte[] skuData = CommonUtil.toByteArray(new FileInputStream(file));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentLength(skuData.length);
            return ResponseEntity.ok().headers(httpHeaders).body(skuData);
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

    @PostMapping("/results/delete")
    @ResponseBody
    public QATestResultWsDto deleteDataSource(@RequestBody QATestResultWsDto qaTestResultWsDto) {
        for (QATestResultDto qaTestResultDto : qaTestResultWsDto.getQaTestResults()) {
            fileService.deleteFilesByQaResultId(qaTestResultDto.getIdentifier());
        }
        qaTestResultWsDto.setMessage("Data deleted successfully!!");
        return qaTestResultWsDto;
    }

    @PostMapping("/sendMail")
    @ResponseBody
    public EmailWsDto sendMail(@RequestBody EmailWsDto emailWsDto) {
        CronJobProfile cronjobProfile = cronJobProfileRepository.findByIdentifier(emailWsDto.getProfileId());
        List<QATestResult> qaTestResults = new ArrayList<>();
        for (EmailDto emailDto : emailWsDto.getEmails()) {
            QATestResult qaTestResult = qaRepository.findByIdentifier(emailDto.getIdentifier());
            if (qaTestResult != null) {
                qaTestResults.add(qaTestResult);
            }
        }
        if (cronjobProfile != null) {
            mailService.sendMailBulkResults(emailWsDto.getSubject(), cronjobProfile.getRecipients(), qaTestResults);
        }
        emailWsDto.setMessage("Mail sent successfully!!");
        return emailWsDto;
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
    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody QATestResultWsDto qaTestResultWsDto) {
        return getConfiguredAttributes(qaTestResultWsDto.getNode());
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new QATestResult());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.SCHEDULER);
    }

    @PostMapping(value = "/results")
    @ResponseBody
    public QATestResultWsDto QATestResultDto(@RequestBody QATestResultWsDto qaTestResultWsDto) {
        qaTestResultWsDto.setSortField("creationTime");
        Pageable pageable = getPageable(qaTestResultWsDto.getPage(), qaTestResultWsDto.getSizePerPage(), qaTestResultWsDto.getSortDirection(), qaTestResultWsDto.getSortField());
        QATestResultDto qaTestResultDto = CollectionUtils.isNotEmpty(qaTestResultWsDto.getQaTestResults()) ? qaTestResultWsDto.getQaTestResults().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(qaTestResultDto, qaTestResultWsDto.getOperator());
        QATestResult qaTestResult = qaTestResultDto != null ? modelMapper.map(qaTestResultDto, QATestResult.class) : null;
        Page<QATestResult> page = isSearchActive(qaTestResult) != null ? qaRepository.findAll(Example.of(qaTestResult, exampleMatcher), pageable) : qaRepository.findAll(pageable);
        Type listType = new TypeToken<List<QATestResultDto>>() {
        }.getType();
        qaTestResultWsDto.setQaTestResults(modelMapper.map(page.getContent(), listType));
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
        Type listType = new TypeToken<List<QALocatorResultReportDto>>() {
        }.getType();
        qaLocatorResultReportWsDto.setLocatorResultReportDtoList(modelMapper.map(page.getContent(), listType));
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
        Pageable pageable = getPageable(start / length, length, Sort.Direction.DESC.name(), "id");
        Page<QATestResult> page = getQaTestResults(startDate, endDate, testName, pageable);
        return gson.toJson(page.getContent());
    }

    @RequestMapping(value = "/reports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    @ResponseBody
    public String processApiReport(@RequestParam(value = "start", required = false, defaultValue = "0") Integer start, @RequestParam(value = "length", required = false, defaultValue = "100") Integer length, @RequestParam(value = "startDate", required = false) String startDate,
                                   @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "testCaseId", required = false) String testCaseId) {
        Pageable pageable = getPageable(start / length, length, Sort.Direction.DESC.name(), "id");
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

    @GetMapping("/results/edit")
    public String editResult(@RequestParam("id") String id, Model model) {
        QATestResult qaTestResult = qaRepository.findByIdentifier(id);
        //model.addAttribute("errorTypes", errorTypeRepository.findAll());
        if (qaTestResult != null) {
            QATestResultForm qaTestResultForm = modelMapper.map(qaTestResult, QATestResultForm.class);
            //model.addAttribute("editForm", qaTestResultForm);
        }
        return "qa/qaedit";
    }

    @PostMapping("/results/edit")
    public String editResult(@ModelAttribute("editForm") QATestResultForm qaTestResultForm, Model model) {
        QATestResult qaTestResult = qaRepository.findByIdentifier(qaTestResultForm.getIdentifier());
        if (qaTestResult != null) {
            qualityAssuranceService.saveErrorType(qaTestResultForm, qaTestResult);
        }
        return "redirect:/qa/results";
    }

    @GetMapping("/results/repair")
    public String repairResult(@RequestParam("id") String id, Model model) {
        QATestResult qaTestResult = qaRepository.findByIdentifier(id);
        if (qaTestResult != null) {
            QATestResultForm qaTestResultForm = modelMapper.map(qaTestResult, QATestResultForm.class);
            // model.addAttribute("editForm", qaTestResultForm);
        }
        return "qa/repair";
    }

    @PostMapping("/results/repair")
    public String repairResult(@ModelAttribute("editForm") QATestResultForm qaTestResultForm, Model model) {
        QATestResult qaTestResult = qaRepository.findByIdentifier(qaTestResultForm.getIdentifier());
        if (qaTestResult != null) {
            qaTestResult.setResultStatus(qaTestResultForm.getResultStatus());
            qaRepository.save(qaTestResult);
        }
        return "redirect:/qa/results";
    }

}
