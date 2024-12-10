package com.touchmind.web.controllers;

import com.touchmind.core.HotFolderConstants;
import com.touchmind.core.mongo.dto.CategoryDto;
import com.touchmind.core.mongo.dto.EnvironmentDto;
import com.touchmind.core.mongo.dto.QATestResultDto;
import com.touchmind.core.mongo.dto.QATestResultWsDto;
import com.touchmind.core.mongo.dto.SiteDto;
import com.touchmind.core.mongo.dto.TestPlanDto;
import com.touchmind.core.mongo.dto.TestPlanWsDto;
import com.touchmind.core.mongo.dto.TestProfileDto;
import com.touchmind.core.mongo.model.Category;
import com.touchmind.core.mongo.model.CronJobProfile;
import com.touchmind.core.mongo.model.Environment;
import com.touchmind.core.mongo.repository.CategoryRepository;
import com.touchmind.core.mongo.repository.CronJobProfileRepository;
import com.touchmind.core.mongo.repository.EnvironmentRepository;
import com.touchmind.core.mongo.repository.SiteRepository;
import com.touchmind.core.mongo.repository.SubsidiaryRepository;
import com.touchmind.core.mongo.repository.TestProfileRepository;
import com.touchmind.core.mongotemplate.QATestResult;
import com.touchmind.core.mongotemplate.repository.QARepository;
import com.touchmind.core.service.ExcelFileService;
import com.touchmind.core.service.FileService;
import com.touchmind.core.service.SiteService;
import com.touchmind.core.service.SubsidiaryService;
import com.touchmind.core.service.TestPlanService;
import com.touchmind.mail.service.MailService;
import com.touchmind.qa.service.QualityAssuranceService;
import com.touchmind.utils.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/qa")
public class QualityAssuranceController extends QaBaseController {

    public static final String DASHBOARD = "dashboard";
    public static final String CURRENT_USER = "currentUser";
    private static final Logger LOG = LoggerFactory.getLogger(QualityAssuranceController.class);
    @Autowired
    private QARepository qaRepository;
    @Autowired
    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private SubsidiaryService subsidiaryService;
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
    private ExcelFileService excelFileService;
    @Autowired
    private CronJobProfileRepository cronJobProfileRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
    List<Environment> getEnvironmentsForSubsidiary(@RequestBody EnvironmentDto environmentDto) {
        return environmentRepository.findByStatusOrderByIdentifier(true).stream().filter(environment -> environment.getSubsidiaries().contains(environmentDto.getRecordId())).collect(Collectors.toList());
    }

    @RequestMapping(value = "/getTestProfileForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<TestProfileDto> getTestProfileForSubsidiary(@RequestBody TestProfileDto testProfileDto) {
        return modelMapper.map(testProfileRepository.findBySubsidiaryOrderByIdentifier(testProfileDto.getRecordId(), true), List.class);
    }

    @RequestMapping(value = "/getCategoryForSubsidiary", method = RequestMethod.POST)
    public @ResponseBody
    List<Category> getCategoryForSubsidiary(@RequestBody CategoryDto categoryDto) {
        return categoryRepository.findByStatusOrderByIdentifier(true).stream().filter(category -> category.getSubsidiaries().contains(categoryDto.getRecordId())).collect(Collectors.toList());
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

    @GetMapping(value = "/failedSkus", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> failedSkus(@RequestBody TestPlanWsDto testPlanWsDto) throws IOException {
        try {
            //TODO
            QATestResult testResults = null;
            //QATestResult testResults = qaRepository.findByRecordId(testPlanWsDto.getTestPlans().get(0).getRecordId());
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
            fileService.deleteFilesByQaResultId(qaTestResultDto.getRecordId());
        }
        qaTestResultWsDto.setMessage("Delete Success");
        return qaTestResultWsDto;
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
}
