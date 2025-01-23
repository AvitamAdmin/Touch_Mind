package com.touchmind.web.controllers.admin.qa;


import com.touchmind.core.mongo.dto.SavedQueryDto;
import com.touchmind.core.mongo.dto.SearchDto;
import com.touchmind.core.mongo.dto.TestPlanDto;
import com.touchmind.core.mongo.dto.TestPlanWsDto;
import com.touchmind.core.mongo.model.TestPlan;
import com.touchmind.core.mongo.repository.EntityConstants;
import com.touchmind.core.mongo.repository.QaTestPlanRepository;
import com.touchmind.core.service.BaseService;
import com.touchmind.core.service.TestPlanService;
import com.touchmind.fileimport.service.FileExportService;
import com.touchmind.fileimport.service.FileImportService;
import com.touchmind.fileimport.strategies.EntityType;
import com.touchmind.web.controllers.BaseController;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/qa")
public class TestPlanConfiguratorController extends BaseController {

    public static final String ADMIN_QA = "/admin/qa";
    Logger logger = LoggerFactory.getLogger(TestPlanConfiguratorController.class);
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private QaTestPlanRepository qaTestPlanRepository;
    @Autowired
    private FileImportService fileImportService;
    @Autowired
    private FileExportService fileExportService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BaseService baseService;

    @PostMapping
    @ResponseBody
    public TestPlanWsDto getTestPlans(@RequestBody TestPlanWsDto testPlanWsDto) {
        Pageable pageable = getPageable(testPlanWsDto.getPage(), testPlanWsDto.getSizePerPage(), testPlanWsDto.getSortDirection(), testPlanWsDto.getSortField());
        TestPlanDto testPlanDto = CollectionUtils.isNotEmpty(testPlanWsDto.getTestPlans()) ? testPlanWsDto.getTestPlans().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testPlanDto, testPlanWsDto.getOperator());
        TestPlan testPlan = testPlanDto != null ? modelMapper.map(testPlanDto, TestPlan.class) : null;
        Page<TestPlan> page = isSearchActive(testPlan) != null ? qaTestPlanRepository.findAll(Example.of(testPlan, exampleMatcher), pageable) : qaTestPlanRepository.findAll(pageable);
        testPlanWsDto.setTestPlans(modelMapper.map(page.getContent(), List.class));
        testPlanWsDto.setBaseUrl(ADMIN_QA);
        testPlanWsDto.setTotalPages(page.getTotalPages());
        testPlanWsDto.setTotalRecords(page.getTotalElements());
        testPlanWsDto.setAttributeList(getConfiguredAttributes(testPlanWsDto.getNode()));
        testPlanWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_PLAN));
        return testPlanWsDto;
    }

    @GetMapping("/getAdvancedSearch")
    @ResponseBody
    public List<SearchDto> getSearchAttributes() {
        return getGroupedParentAndChildAttributes(new TestPlan());
    }

    @PostMapping("/saveSearchQuery")
    @ResponseBody
    public String savedQuery(@RequestBody SavedQueryDto savedQueryDto) {
        return baseService.saveSearchQuery(savedQueryDto, EntityConstants.TEST_PLAN);
    }

    @GetMapping("/get")
    public TestPlanWsDto getActiveTestPlans() {
        TestPlanWsDto testPlanWsDto = new TestPlanWsDto();
        testPlanWsDto.setTestPlans(modelMapper.map(qaTestPlanRepository.findByStatusOrderByIdentifier(true), List.class));
        testPlanWsDto.setBaseUrl(ADMIN_QA);
        return testPlanWsDto;
    }

    @RequestMapping(value = "/getByRecordId", method = RequestMethod.GET)
    public @ResponseBody TestPlanDto getByRecordId(@RequestParam("recordId") String recordId) {
        return modelMapper.map(qaTestPlanRepository.findByRecordId(recordId), TestPlanDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestPlanWsDto updateTestPlan(@RequestBody TestPlanWsDto request) {
        testPlanService.handleEdit(request);
        request.setBaseUrl(ADMIN_QA);
        return request;
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestPlanWsDto deleteTestPlan(@RequestBody TestPlanWsDto testPlanWsDto) {
        for (TestPlanDto testPlanDto : testPlanWsDto.getTestPlans()) {
            qaTestPlanRepository.deleteByRecordId(testPlanDto.getRecordId());
        }
        testPlanWsDto.setMessage("Data deleted successfully!!");
        testPlanWsDto.setBaseUrl(ADMIN_QA);
        return testPlanWsDto;
    }

    @PostMapping("/getedits")
    @ResponseBody
    public TestPlanWsDto editMultiple(@RequestBody TestPlanWsDto request) {
        List<TestPlanDto> testPlanDtos = request.getTestPlans();
        request.setBaseUrl(ADMIN_QA);
        List<TestPlan> testPlans = new ArrayList<>();
        for (TestPlanDto testPlanDto : testPlanDtos) {
            testPlans.add(qaTestPlanRepository.findByRecordId(testPlanDto.getRecordId()));
        }
        request.setTestPlans(modelMapper.map(testPlans, List.class));
        request.setRedirectUrl("/admin/qa");
        request.setBaseUrl(ADMIN_QA);
        return request;
    }

    @PostMapping("/upload")
    @ResponseBody
    public TestPlanWsDto uploadFile(@RequestBody MultipartFile file) {
        TestPlanWsDto testPlanWsDto = new TestPlanWsDto();
        try {
            fileImportService.importFile(file, EntityType.ENTITY_IMPORT_ACTION, EntityConstants.TEST_PLAN_CONFIGURATION, EntityConstants.TEST_PLAN, testPlanWsDto);
            if (StringUtils.isEmpty(testPlanWsDto.getMessage())) {
                testPlanWsDto.setMessage("File uploaded successfully!!");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return testPlanWsDto;
    }

    @GetMapping("/export")
    @ResponseBody
    public TestPlanWsDto uploadFile() {
        TestPlanWsDto testPlanWsDto = new TestPlanWsDto();
        try {
            testPlanWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_PLAN_CONFIGURATION));
            return testPlanWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
