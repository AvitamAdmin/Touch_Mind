package com.touchMind.web.controllers.admin.qa;


import com.touchMind.core.mongo.dto.SavedQueryDto;
import com.touchMind.core.mongo.dto.SearchDto;
import com.touchMind.core.mongo.dto.TestPlanDto;
import com.touchMind.core.mongo.dto.TestPlanWsDto;
import com.touchMind.core.mongo.model.TestPlan;
import com.touchMind.core.mongo.repository.EntityConstants;
import com.touchMind.core.mongo.repository.QaTestPlanRepository;
import com.touchMind.core.mongo.repository.TestLocatorGroupRepository;
import com.touchMind.core.service.BaseService;
import com.touchMind.core.service.TestPlanService;
import com.touchMind.fileimport.service.FileExportService;
import com.touchMind.fileimport.service.FileImportService;
import com.touchMind.fileimport.strategies.EntityType;
import com.touchMind.web.controllers.BaseController;
import com.google.common.reflect.TypeToken;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
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
//    @Autowired
//    private SubsidiaryRepository subsidiaryRepository;
    @Autowired
    private TestLocatorGroupRepository testLocatorGroupRepository;

    @PostMapping
    @ResponseBody
    public TestPlanWsDto getTestPlans(@RequestBody TestPlanWsDto testPlanWsDto) {
        Pageable pageable = getPageable(testPlanWsDto.getPage(), testPlanWsDto.getSizePerPage(), testPlanWsDto.getSortDirection(), testPlanWsDto.getSortField());
        TestPlanDto testPlanDto = CollectionUtils.isNotEmpty(testPlanWsDto.getTestPlans()) ? testPlanWsDto.getTestPlans().get(0) : null;
        ExampleMatcher exampleMatcher = getMatcher(testPlanDto, testPlanWsDto.getOperator());
        TestPlan testPlan = testPlanDto != null ? modelMapper.map(testPlanDto, TestPlan.class) : null;
        Page<TestPlan> page = isSearchActive(testPlan) != null ? qaTestPlanRepository.findAll(Example.of(testPlan, exampleMatcher), pageable) : qaTestPlanRepository.findAll(pageable);
        Type listType = new TypeToken<List<TestPlanDto>>() {
        }.getType();
        List<TestPlanDto> testPlanDtoList = new ArrayList<>();
        for (TestPlan testPlan1 : page.getContent()) {
            TestPlanDto testPlanDto1 = modelMapper.map(testPlan1, TestPlanDto.class);
            String sub = testPlan1.getSubsidiary();
            if (StringUtils.isNotEmpty(sub)) {
                testPlanDto1.setSubsidiary(sub);
            }
            testPlanDtoList.add(testPlanDto1);
        }
        testPlanWsDto.setTestPlans(testPlanDtoList);
        testPlanWsDto.setBaseUrl(ADMIN_QA);
        testPlanWsDto.setTotalPages(page.getTotalPages());
        testPlanWsDto.setTotalRecords(page.getTotalElements());
        testPlanWsDto.setAttributeList(getConfiguredAttributes(testPlanWsDto.getNode()));
        testPlanWsDto.setSavedQuery(baseService.getSavedQuery(EntityConstants.TEST_PLAN));
        return testPlanWsDto;
    }

    @PostMapping("/getSearchQuery")
    @ResponseBody
    public List<SearchDto> savedQuery(@RequestBody TestPlanWsDto testPlanWsDto) {
        return getConfiguredAttributes(testPlanWsDto.getNode());
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
        Type listType = new TypeToken<List<TestPlanDto>>() {
        }.getType();
        testPlanWsDto.setTestPlans(modelMapper.map(qaTestPlanRepository.findByStatusOrderByIdentifier(true), listType));
        testPlanWsDto.setBaseUrl(ADMIN_QA);
        return testPlanWsDto;
    }

    @RequestMapping(value = "/getByIdentifier", method = RequestMethod.GET)
    public @ResponseBody TestPlanDto getByIdentifier(@RequestParam("recordId") String recordId) {
        return modelMapper.map(qaTestPlanRepository.findByIdentifier(recordId), TestPlanDto.class);
    }

    @PostMapping("/edit")
    @ResponseBody
    public TestPlanWsDto updateTestPlan(@RequestBody TestPlanWsDto request) {
        request = testPlanService.handleEdit(request);
        request.setBaseUrl(ADMIN_QA);
        return request;
    }

    @PostMapping("/delete")
    @ResponseBody
    public TestPlanWsDto deleteTestPlan(@RequestBody TestPlanWsDto testPlanWsDto) {
        for (TestPlanDto testPlanDto : testPlanWsDto.getTestPlans()) {
            qaTestPlanRepository.deleteByIdentifier(testPlanDto.getIdentifier());
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
            testPlans.add(qaTestPlanRepository.findByIdentifier(testPlanDto.getIdentifier()));
        }
        Type listType = new TypeToken<List<TestPlanDto>>() {
        }.getType();
        request.setTestPlans(modelMapper.map(testPlans, listType));
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

    @PostMapping("/export")
    @ResponseBody
    public TestPlanWsDto uploadFile(@RequestBody TestPlanWsDto testPlanWsDto) {

        try {
            testPlanWsDto.setFileName(File.separator + "impex" + fileExportService.exportEntity(EntityConstants.TEST_PLAN_CONFIGURATION, testPlanWsDto.getHeaderFields()));
            return testPlanWsDto;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
